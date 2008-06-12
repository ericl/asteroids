/*
 * Asteroids - APCS Final Project
 *
 * This source is provided under the terms of the BSD License.
 *
 * Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The authors' names may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package asteroids.weapons;
import java.util.*;
import java.lang.reflect.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import asteroids.bodies.Ship;
import asteroids.handlers.*;
import asteroids.handlers.Timer;
import static asteroids.Util.*;

/**
 * Handles mechanics of weapons fire from ships.
 */
public class WeaponSys {
	protected static float ANGULAR_DISTRIBUTION = (float)Math.PI/48;
	protected List<Stats> stats = new LinkedList<Stats>();
	protected Ship s;
	protected World world;
	protected Weapon weapon;
	protected Constructor<Weapon> cons;
	protected long lastFired;
	protected Queue<Weapon> fired = new LinkedList<Weapon>();
	protected float burst;

	public WeaponSys(Ship ship, World wo, Weapon w) {
		setWeaponType(w);
		s = ship;
		world = wo;
	}

	public void setRandomWeaponType() {
		switch ((int)(2*Math.random())) {
			case 0: setWeaponType(new Laser()); break;
			case 1: setWeaponType(new Laser2()); break;
		}
	}

	public void upgrade() {
		if (weapon.getLevel() < Weapon.MAX_LEVEL)
			weapon.incrementLevel();
		else if (!(weapon instanceof Laser2))
			setWeaponType(new Laser2());
	}

	@SuppressWarnings(value = "unchecked")
	public void setWeaponType(Weapon w) {
		weapon = w;
		lastFired = 0;
		try {
			cons = (Constructor<Weapon>)weapon.getClass().getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public boolean canFire() {
		long current = Timer.gameTime();
		long disp = current - lastFired;
		float test = Math.min(disp / weapon.getReloadTime(), weapon.getBurstLength());
		if (test > burst)
			burst = test;
		if (burst > 1 && disp > weapon.getReloadTime() / 4
				|| disp > weapon.getReloadTime()) {
			burst--;
			lastFired = current;
			return true;
		}
		return false;
	}
	
	public void fire() {
		if (!canFire())
			return;
		float initialAngle = (weapon.getNum()-1)*ANGULAR_DISTRIBUTION/2;
		for (int i=0; i < weapon.getNum(); i++) {
			Weapon weap = makeWeapon(i*ANGULAR_DISTRIBUTION - initialAngle);
			world.add(weap);
			fired.add(weap);
		}
	}

	public void addStatsListener(Stats s) {
		stats.add(s);
	}

	// postcondition: nothing is modified
	private Weapon makeWeapon(float angle) {
		Weapon c = null;
		try {
			c = cons.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.setOrigin(s);
		c.setLevel(weapon.getLevel());
		c.setRotation(s.getRotation()+angle);
		float xc = (float)Math.sin(s.getRotation()+angle);
		float yc = (float)Math.cos(s.getRotation()+angle);
		float sr = s.getRadius() * 2 / 3; // estimated length
		c.setPosition(s.getPosition().getX()+xc*sr,s.getPosition().getY()-yc*sr);
		c.adjustVelocity(v(weapon.getSpeed()*xc,weapon.getSpeed()*-yc));
		c.adjustVelocity((Vector2f)s.getVelocity());
		c.addExcludedBody(s);
		BodyList el = s.getExcludedList();
		for (int i=0; i < el.size(); i++)
			c.addExcludedBody(el.get(i));
		for (Weapon f : fired)
			c.addExcludedBody(f);
		for (Stats stat : stats)
			stat.fired(c);
		return c;
	}
	
	public void update() {
		while (!fired.isEmpty() && fired.peek().exploded()) {
			Weapon w = fired.remove();
			world.remove(w);
			s.removeExcludedBody(w);
		}
	}
}
