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

import asteroids.ai.AI;
import asteroids.ai.Automated;
import asteroids.ai.ShipAI;

import net.phys2d.raw.*;


/**
 * User-controlled ship in the world.
 */
public class ComputerShip extends Ship implements Automated {
	private AI ai;
	private boolean delay;

	public ComputerShip(World w) {
		super(w);
		ai = new ShipAI(w, this);
	}

	public ComputerShip(World w, boolean delay) {
		super(w);
		ai = new ShipAI(w, this);
		this.delay = delay;
		if (delay)
			activeTime = ACTIVE_DEFAULT;
	}

	public void modifyTorque(float t) {
		torque = t;
	}

	public void reset() {
		super.reset();
		activeTime = delay ? ACTIVE_DEFAULT : 0;
	}

	public boolean fire() {
		return weapons.fire();
	}

	protected void torque() {
		// setTorque() is unpredictable with varied dt
		adjustAngularVelocity(getMass()*torque);
	}


	public float getWeaponSpeed() {
		return weapons.getWeaponSpeed();
	}

	public void setAccel(float a) {
		accel = A*a;
	}

	public double health() {
		return hull / MAX;
	}

	public void endFrame() {
		super.endFrame();
		if (activeTime-- < 1)
			ai.update();
	}
}
