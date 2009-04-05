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
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;

import asteroids.ai.*;
import asteroids.bodies.*;
import static asteroids.Util.*;

public class Missile extends Weapon implements Automated {
	private static float myRadius = 2;
	private HomingAI ai;
	private float torque;
	private static World world;
	private boolean explode;

	public Missile() {
		super(new Circle(myRadius), 1000);
		ai = new HomingAI(world, this);
		setMaxVelocity(40, 40);
		setRotDamping(100);
	}

	public void setAccel(float a) {}
	public void modifyTorque(float t) {
		torque = t;
	}

	public float getWeaponSpeed() {
		return 55;
	}

	public boolean canTarget() {
		return false;
	}

	public int getPointValue() {
		return 0;
	}

	public boolean launchMissile() {
		return false;
	}

	public boolean fire() {
		return false;
	}

	public double health() {
		return 1;
	}

	public static void setWorld(World w) {
		world = w;
	}

	public void addExcludedBody(Body b) {
		ai.addExcluded(b);
		super.addExcludedBody(b);
	}

	public void endFrame() {
		super.endFrame();
		Vector2f dir = direction(getRotation());
		addForce(v(20*getMass()*dir.getX(),20*getMass()*dir.getY()));
		adjustAngularVelocity(getMass()*torque);
		ai.update();
	}

	public Body getRemnant() {
		return new LargeExplosion(.5f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return .7f;
	}

	public Vector2f getTextureCenter() {
		return v(3,3);
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Ship)
			explode = true;
		if (other instanceof Missile && ((Missile)other).getOrigin() != ship)
			explode = true;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getTextureScaleFactor() {
		return .75f;
	}

	public int getBurstLength() {
		return Math.min(4, level);
	}

	public String getTexturePath() {
		return "pixmaps/blast.png";
	}

	public float getLaunchSpeed() {
		return 55;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1;
	}

	public float getReloadTime() {
		return 100;
	}
}
