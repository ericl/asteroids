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

package asteroids.bodies;

import java.awt.event.*;

import asteroids.display.*;

import net.phys2d.math.Vector2f;

import net.phys2d.raw.*;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

/**
 * User-controlled ship in the world.
 */
public class ComputerShip extends Ship implements Drawable, Textured, Explodable, KeyListener {
	private int steps;
	private Body target;

	public ComputerShip(World w) {
		super(w);
	}

	public void randomAcceleration() {
		accel = A*range(0,20);
	}

	private void selectTarget() {
		float min_dist = -1;
		target = null;
		BodyList bodies = world.getBodies();
		float d = 0;
		for (int i=0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b == this || !(b instanceof Ship))
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

	private boolean trackTarget() {
		if (target == null)
			return false;
		Vector2f ds = sub(getPosition(), target.getPosition());
		float x = ds.getX();
		float y = ds.getY();
		double tFinal = Math.atan2(y, x) - Math.PI/2;
		double tInit1 = (getRotation() % (2*Math.PI));
		double tInit2 = tInit1 - sign((float)tInit1)*2*Math.PI;
		double delta1 = tFinal - tInit1;
		double delta2 = tFinal - tInit2;
		double delta = Math.abs(delta1) > Math.abs(delta2) ? delta2 : delta1;
		torque = (float)(delta * 8e-5f);
		if (torque > 0 && torque < 1e-5f) {
			torque = 1e-5f;
			return true;
		}
		if (torque < 0 && torque > -1e-5f) {
			torque = -1e-5f;
			return true;
		}
		return false;
	}

	public void reset() {
		super.reset();
		notifyInput();
	}

	private void aiUpdate() {
		steps++;
		float v = getVelocity().length();
		setDamping(v < 50 ? 0 : v < 100 ? .1f : .5f);
		thrust--;
		if (steps % 100 == 66)
			randomAcceleration();
		if (steps % 300 == 0)
			selectTarget();
		if (steps % 10 == 0)
			if (trackTarget())
				weapons.fire();
	}

	public void endFrame() {
		super.endFrame();
		if (activeTime < 1)
			aiUpdate();
		activeTime--;
	}
}
