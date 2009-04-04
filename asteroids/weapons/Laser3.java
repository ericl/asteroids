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
import static asteroids.Util.*;

/**
 * Powerful, fast-moving blast.
 */
public class Laser3 extends Weapon {
	private static float myRadius = 3;
	private boolean explode;

	public Laser3() {
		super(new Circle(myRadius), 100);
		setRestitution(1);
	}

	public Body getRemnant() {
		return new LaserExplosion(2);
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return 1.3f;
	}

	public Vector2f getTextureCenter() {
		return v(21,20);
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		explode = !(other instanceof Weapon) || other instanceof Laser3;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getTextureScaleFactor() {
		return 1f;
	}

	public int getBurstLength() {
		return Math.min(4, level);
	}

	public String getTexturePath() {
		return "pixmaps/exp2/1.png";
	}

	public float getSpeed() {
		return 60;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1 + Math.min(2, level) / 2;
	}

	public float getReloadTime() {
		return 1200;
	}
}
