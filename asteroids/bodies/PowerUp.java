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

import java.awt.Color;

import java.util.*;

import asteroids.weapons.*;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

/**
 * Body that adjust ship attributes on collision.
 */
public abstract class PowerUp extends Body implements Explodable {
	protected boolean explode;

	public static PowerUp random() {
		switch ((int)(20*Math.random())) {
			case 0:
			case 1: return new RandomWeapon();
			case 2:
			case 3: return new Invincibility();
			case 4:
			case 5:
			case 6:
			case 7: return new WeaponPower();
			case 8:
			case 9:
			case 10:
			case 11:
			case 12: return new MissilePower();
			default: return new ArmorRecovery();
		}
	}

	public PowerUp(Polygon shape) {
		super(shape, shape.getArea());
		setDamping(1);
		setMaxVelocity(20, 20);
	}

	public Color getColor() {
		return Color.GREEN;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Ship) {
			up((Ship)other);
			explode = true;
		}
	}

	public PowerUp(DynamicShape shape) {
		super(shape, 1e-10f);
	}

	/**
	 * @param	ship	The ship to be powered up.
	 */
	protected abstract void up(Ship ship);

	public abstract float getRadius();

	public Body getRemnant() {
		return new PowerUpExplosion();
	}

	public List<Body> getFragments() {
		return null;
	}
	
	public boolean canExplode() {
		return explode;
	}
}
