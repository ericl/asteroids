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
import java.util.*;
import java.awt.Color;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

/**
 * Fuzzy-looking asteroids that shrinks instead of exploding.
 */
public class IceAsteroid extends CircleAsteroid implements Textured {
	private double melting;
	private int count = 0;
	private boolean rocky = oneIn(4);
	private static int ICE_TO_ROCK_RADIUS = 12;

	public IceAsteroid(float radius) {
		super(radius);
		color = Color.CYAN;
	}

	public Vector2f getTextureCenter() {
		return v(100,100);
	}

	public float getTextureScaleFactor() {
		return getRadius()/66.66f;
	}

	public String getTexturePath() {
		return rocky && getRadius() < 20 ? "pixmaps/1.png" : "pixmaps/fog.png";
	}

	public void endFrame() {
		super.endFrame();
		count++;
		if (melting - 1 > 0 && count % 5 == 0) {
			melting--;
			if (rocky && getRadius() > ICE_TO_ROCK_RADIUS)
				setShape(new Circle(getRadius()*2/3 - 1));
			else if (!rocky && getRadius() > 7)
				setShape(new Circle(getRadius()*2/3 - 1));
		}
	}

	public float getRadius() {
		return super.getRadius()/2*3;
	}

	public void collided(CollisionEvent e) {
		melting += 50 * Exploder.getDamage(e, this);
	}

	public boolean canExplode() {
		return getRadius() < 7 || (rocky && getRadius() <= ICE_TO_ROCK_RADIUS);
	}

	public Body getRemnant() {
		return rocky ? null : PowerUp.random();
	}

	public List<Body> getFragments() {
		if (!rocky || getRadius() < 7)
			return null;
		List<Body> f = new ArrayList<Body>(6);
		damage = Float.POSITIVE_INFINITY;
		Asteroid tmp;
		for (int i=0; i < 5; i++) {
			tmp = new SmallAsteroid(getRadius() / 3);
			f.add(tmp);
		}
		for (int i=0; i < 5; i++) {
			tmp = new IceAsteroid(getRadius() / 6);
			f.add(tmp);
		}
		return f;
	}
}
