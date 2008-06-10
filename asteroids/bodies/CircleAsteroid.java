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
import java.awt.Graphics2D;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.display.*;
import static asteroids.Util.*;

public class CircleAsteroid extends Asteroid implements Drawable {
	private Color color = Color.orange;

	public CircleAsteroid(float radius) {
		super(new Circle(radius));
	}

	public void setColor(Color c) {
		color = c;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(getRadius() < 100 ? color : Color.darkGray);
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public float getRadius() {
		return ((Circle)getShape()).getRadius();
	}

	public Body getRemnant() {
		CircleAsteroid x = new CircleAsteroid(getRadius() / 2);
		x.setColor(color);
		return getRadius() > 15 ? x : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(4);
		int max = (int)(getRadius() > 30 ? range(4,6) : range(3,4));
		CircleAsteroid x = null;
		if (getRadius() > 10)
			for (int i=0; i < max; i++) {
				f.add(x = new CircleAsteroid(getRadius() / 3));
				x.setColor(color);
			}
		powerup(f);
		return f;
	}
}
