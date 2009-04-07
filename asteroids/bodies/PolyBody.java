/**
 * An abstract polygonal asteroid that calculates scaling, center of mass,
 * and geometry automatically.
 */

package asteroids.bodies;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.display.*;

import net.phys2d.math.*;

import net.phys2d.raw.shapes.*;

public abstract class PolyBody extends PolyBodyProxy implements Drawable {
	private float radius;
	private Color color = Color.RED;
	private Vector2f centroid;
	protected float ratio;

	// ratio is the scaling of the polygon
	public PolyBody(ROVector2f[] raw, float scale) {
		super(new Polygon(centralized(scaled(raw, scale))));
		AABox a = getShape().getBounds();
		ratio = scale;
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
		setRestitution(.5f);
	}

	public PolyBody(ROVector2f[] raw, float scale, float mass) {
		super(new Polygon(centralized(scaled(raw, scale))), mass);
		AABox a = getShape().getBounds();
		ratio = scale;
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
		setRestitution(.5f);
	}

	public void setColor(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Polygon poly = (Polygon)getShape();
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.setColor(color);
		g2d.fillPolygon(xcoords, ycoords, verts.length);
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
	private static ROVector2f[] centralized(ROVector2f[] in) {
		ROVector2f c = new Polygon(in).getCentroid();
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.sub(in[i], c);
		return out;
	}

	/**
	 * Scales an entire ROVector2f[] by an amount.
	 */
	private static ROVector2f[] scaled(ROVector2f[] in, float scalefactor) {
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.scale(in[i], scalefactor);
		return out;
	}
}
