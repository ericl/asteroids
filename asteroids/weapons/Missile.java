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

import asteroids.bodies.*;
import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

public class Missile extends Weapon {
	private static float myRadius = 2;
	private Set<Body> whitelist = new HashSet<Body>();
	private int steps;
	private float torque;
	private static World world;
	private boolean explode;
	private Body target;
	private ROVector2f targetPos;

	public Missile() {
		super(new Circle(myRadius), 1000);
		setMaxVelocity(40, 40);
		setRotDamping(200);
	}

	public static void setWorld(World w) {
		world = w;
	}

	public void addExcludedBody(Body b) {
		whitelist.add(b);
		super.addExcludedBody(b);
	}

	private void aiUpdate() {
		if (steps % 100 == 0) {
			selectTarget();
		}
		if (steps % 30 == 0)
			trackTarget();
		steps++;
	}

	private void trackTarget() {
		if (target == null)
			return;
		if (target instanceof Ship) {
			if (!((Ship)target).isCloaked())
				targetPos = new Vector2f(target.getPosition());
		} else {
			targetPos = target.getPosition();
		}
		if (targetPos == null)
			return;
		Vector2f ds = sub(getPosition(), targetPos);
		double tFinal = Math.atan2(ds.getY(), ds.getX()) - Math.PI/2;
		double tInit1 = (getRotation() % (2*Math.PI));
		double tInit2 = tInit1 - sign((float)tInit1)*2*Math.PI;
		double delta1 = tFinal - tInit1;
		double delta2 = tFinal - tInit2;
		double delta = Math.abs(delta1) > Math.abs(delta2) ? delta2 : delta1;
		torque = (float)(delta * 1e-5f);
		if (torque > 0 && torque < 1e-5f)
			torque = 1e-5f;
		if (torque < 0 && torque > -1e-5f)
			torque = -1e-5f;
	}

	private void selectTarget() {
		float min_dist = -1;
		target = null;
		BodyList bodies = world.getBodies();
		float d = 0;
		for (int i=0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
//			if (b instanceof Missile && b != this) {
//				if (((Missile)b).whitelist.equals(whitelist))
//					continue; // we're on the same side here
//			} else
			if (b == this || !(b instanceof Ship) || whitelist.contains(b))
				continue;
			if (target == null)
				target = b;
			d = Math.abs(getPosition().distance(b.getPosition()));
			if (min_dist < 0)
				min_dist = d;
			else if (d < min_dist) {
				target = b;
				min_dist = d;
			}
		}
	}

	protected void accel() {
		Vector2f dir = direction(getRotation());
		float accel = 20;
		addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
	}

	protected void torque() {
		// setTorque() is unpredictable with varied dt
		adjustAngularVelocity(getMass()*torque);
	}

	public void endFrame() {
		super.endFrame();
		accel();
		torque();
		aiUpdate();
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
		if (other instanceof Missile && !((Missile)other).whitelist.equals(whitelist))
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

	public float getSpeed() {
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
