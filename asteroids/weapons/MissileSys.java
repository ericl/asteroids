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
import net.phys2d.raw.*;
import net.phys2d.math.Vector2f;
import asteroids.bodies.*;
import asteroids.handlers.Stats;
import static asteroids.Util.v;

/**
 * Just something thrown together quickly
 * basic missile sys capable of only guiding one missile right now
 */
public class MissileSys extends WeaponSys {

	public MissileSys(Ship s, World world, Weapon w) {
		super(s, world, w);
	}
	
	
	/*
	 * create the missile...("launch")
	 */
	public void fire() {
		fired.clear();
		Body target = getVictim();
		
		if (!canFire()) return;
		Weapon c = null;
		try {
			c = cons.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		c.setRotation(s.getRotation());
		float xc = (float)Math.sin(s.getRotation());
		float yc = (float)Math.cos(s.getRotation());
		float sr = s.getRadius() * 2 / 3; // estimated length
		c.setPosition(s.getPosition().getX()+xc*sr,s.getPosition().getY()-yc*sr);
		c.adjustVelocity(v(weapon.getSpeed()*xc,weapon.getSpeed()*-yc));
		c.adjustVelocity((Vector2f)s.getVelocity());
		c.addExcludedBody(s);
		fired.add(c);
		for (Stats stat : stats)
			stat.fired(c);
		world.add(c);
	}
	
	/*
	 * guide the missile...nah...it should just be a unguided rocket...nah...sajflkdsajg;lj
	 * hopefully the user won't be able to spam missiles...
	 */
	
	public void update() {
		Missile missile = (Missile)(fired.peek());
		Vector2f vec = new Vector2f(missile.getTarget().getVelocity());
		vec.add(missile.getVelocity());
		float xd = missile.getVelocity().getX() - missile.getTarget().getVelocity().getX();
		float yd = missile.getVelocity().getY() - missile.getTarget().getVelocity().getY();
		vec.scale(8e5f*missile.getMass());
		if(missile != null) {
			missile.adjustVelocity(new Vector2f(xd * 20, yd * 20));
			missile.addForce(vec);
		}
	}
	
	/*
	 * @return self-explantory
	 */
	protected Body getVictim() {
		BodyList potentialVictims = world.getBodies();
		float temp = s.getPosition().distance(potentialVictims.get(0).getPosition());
		Body target = potentialVictims.get(0);
		
		for(int count = 1; count < potentialVictims.size(); count++) {
			if(s.getPosition().distance(potentialVictims.get(count).getPosition()) < temp) {
				temp = s.getPosition().distance(potentialVictims.get(count).getPosition());
				target = potentialVictims.get(count);
			}
		}
		return target;
		
	}
}
