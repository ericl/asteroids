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

public class Laser2 extends Weapon {
	private static float myRadius = 2;
	private boolean thrust;
	private int steps;

	public Laser2() {
		super(new Circle(myRadius), 1);
		setRestitution(1);
		setMaxVelocity(40,40);
	}

	public Body getRemnant() {
		return new LargeExplosion(.75f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return .6f;
	}

	public void endFrame() {
		super.endFrame();
		if (steps++ > 75) {
			thrust = true;
			if (steps > 1000)
				thrust = false;
		}
		Vector2f dir = direction(getRotation());
		float accel = 20;
		if (thrust) {
			addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
			if (steps < 200)
				setRotation(ship.getRotation());
		}
	}

	public Vector2f getTextureCenter() {
		return v(8,8);
	}

	public float getTextureScaleFactor() {
		return .75f;
	}

	public int getBurstLength() {
		return 5;
	}

	public String getTexturePath() {
		return thrust ?  "pixmaps/rocket-t.png" : "pixmaps/rocket.png";
	}

	public float getSpeed() {
		return 30;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1 + Math.min(2, level);
	}

	public float getReloadTime() {
		return 700;
	}
}
