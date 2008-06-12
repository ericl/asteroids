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

package asteroids.handlers;
import java.awt.*;
import java.util.*;
import net.phys2d.math.*;
import asteroids.display.*;
import static asteroids.Util.*;

/**
 * Wrap around starfield that provides visual consistency at
 * the expense of speed (usually < 1ms)
 */
public class StarField {
	// hoping that no one has really high resolution monitors
	private static int DIMENSION = 3000;
	private static double DENSITY = 2e-4;
	private static Color[] colors = {Color.yellow,Color.orange,Color.cyan};
	private LinkedList<Star> stars = new LinkedList<Star>();
	private Display display;

	public StarField(Display d) {
		display = d;
	}

	/**
	 * creates stars and adds them to the linked list of stars
	 */
	public void init() {
		int numstars = (int)(DIMENSION*DIMENSION*DENSITY);
		stars.clear();
		for (int i=0; i < numstars; i++)
			stars.add(new Star(v(range(0,DIMENSION), range(0,DIMENSION))));
	}
	
	/**
	 * draws the stars on the window screen
	 */
	public void starField() {
		for (Star star : stars)
			display.drawDrawable(star);
	}

	
	/**
	 * @return Returns a random color
	 */
	private static Color starColor() {
		Color c = oneIn(5) ? Color.WHITE : Color.GRAY;
		if (oneIn(10))
			c = colors[(int)range(0,colors.length)];
		return c;
	}

	/**
	 * constructs a star
	 */
	private class Star implements Drawable {
		public final static int MIN_SIZE = 1;
		public final static int MAX_SIZE = 4;
		private Vector2f loc;
		private int radius = (int)range(MIN_SIZE,MAX_SIZE);
		private Color color = starColor();

		public Star(Vector2f v) {
			loc = v;
		}

		public float getRadius() {
			// XXX trick the display
			return Float.POSITIVE_INFINITY;
		}

		public void drawTo(Graphics2D g2d, ROVector2f origin) {
			// wrap around the display
			while (loc.getX() - origin.getX() > DIMENSION)
				loc = MathUtil.sub(loc, v(DIMENSION,0));
			while (loc.getX() - origin.getX() < 0)
				loc = MathUtil.sub(loc, v(-DIMENSION,0));
			while (loc.getY() - origin.getY() > DIMENSION)
				loc = MathUtil.sub(loc, v(0,DIMENSION));
			while (loc.getY() - origin.getY() < 0)
				loc = MathUtil.sub(loc, v(0,-DIMENSION));
			if (display.inView(loc,radius)) {
				Vector2f tmp = MathUtil.sub(loc, origin);
				g2d.setColor(color);
				g2d.fillOval((int)tmp.getX(), (int)tmp.getY(), radius, radius);
			}
		}

		public Vector2f getPosition() {
			return loc;
		}
	}
}
