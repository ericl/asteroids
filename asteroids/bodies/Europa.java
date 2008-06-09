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
import asteroids.*;
import java.util.*;
import java.awt.Color;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;

public class Europa extends CircleAsteroid implements Textured {
	private IceAsteroid core = new IceAsteroid(90);
	public static int MAX = 10;

	public Europa() {
		super(150);
		setMoveable(false);
	}

	public Vector2f getTextureCenter() {
		return v(150,150);
	}

	public Color statusColor() {
		if (damage/MAX < .4)
			return AbstractGame.COLOR;
		else if (damage/MAX < .8)
			return Color.YELLOW;
		return Color.RED;
	}
	
	public boolean canExplode() {
		return damage > MAX;
	}

	public String getPercentDamage() {
		return (int)((1-damage/MAX)*100) + "%";
	}

	public String getTexturePath() {
		return "pixmaps/europa.png";
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		if (getRadius() > 10)
			for (int i=0; i < 6; i++)
				f.add(new HexAsteroid(50));	
		return f;
	}

	public Asteroid getRemnant() {
		core.setMoveable(false);
		return core;
	}
	
	public float getTextureScaleFactor() {
		return getRadius() / 150.0f;
	}
}
