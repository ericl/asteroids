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
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.display.*;
import static asteroids.Util.*;
import static asteroids.bodies.PolyAsteroid.*;

/**
 * 5 extra missiles.
 */
public class MissilePower extends PowerUp implements Textured {
	protected static ROVector2f[] raw = {v(2,4),v(10,1),v(19,19),v(12,21),v(3,19),v(2,5),v(11,1)};
	protected static float RATIO = 1f;
	protected float radius;
	protected Vector2f centroid;

	public MissilePower() {
		super(new Polygon(centralized(scaled(raw, RATIO))));
		AABox a = getShape().getBounds();
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	/**
	 * The rotation of the object is 0 because it does not spin.
	 * @return	The rotation number.
	 */
	public float getRotation() {
		return 0;
	}

	public float getRadius() {
		return radius;
	}

	/**
	 * Upgrades the weapon of the ship.
	 * @param	ship	The Ship to receive the upgrade.
	 */
	protected void up(Ship ship) {
		ship.addMissiles(5);
	}

	public String getTexturePath() {
		return "pixmaps/edit-delete.png";
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}

	public float getTextureScaleFactor() {
		return RATIO;
	}
}
