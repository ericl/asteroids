package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

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
	 * Transforms a ROVector2f[] such that its center of mass will be at v(0,0)
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
