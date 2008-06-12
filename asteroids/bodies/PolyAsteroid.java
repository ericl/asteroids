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
import java.awt.Graphics2D;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.display.*;

/**
 * An abstract polygonal asteroid that calculates scaling, center of mass,
 * and geometry automatically.
 */
public abstract class PolyAsteroid extends Asteroid implements Drawable {
	private float radius;
	private Vector2f centroid;
	protected float ratio;
	protected Color color = Color.blue;

	// ratio is the scaling of the polygon
	public PolyAsteroid(ROVector2f[] raw, float scale) {
		super(new Polygon(centralized(scaled(raw, scale))));
		AABox a = getShape().getBounds();
		ratio = scale;
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Polygon poly = (Polygon)getShape();
		g2d.setColor(color);
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	public void setColor(Color c) {
		color = c;
	}

	public float getRadius() {
		return radius;
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}

	/**
	 * Transforms a ROVector2f[] such that its center of mass will be at v(0,0).
	 */
	public static ROVector2f[] centralized(ROVector2f[] in) {
		ROVector2f c = new Polygon(in).getCentroid();
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.sub(in[i], c);
		return out;
	}

	/**
	 * Scales an entire ROVector2f[] by an amount.
	 */
	public static ROVector2f[] scaled(ROVector2f[] in, float scalefactor) {
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.scale(in[i], scalefactor);
		return out;
	}
}
