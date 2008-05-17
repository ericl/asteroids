package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class PolyAsteroid extends Asteroid implements Drawable {
	private float radius;
	private Vector2f centroid;
	protected float ratio;
	protected Color color = Color.blue;

	public PolyAsteroid(ROVector2f[] raw, float ratio) {
		super(new Polygon(centralized(scaled(raw, ratio))));
		this.ratio = ratio;
		AABox a = getShape().getBounds();
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Polygon poly = (Polygon)getShape();
		g2d.setColor(color);
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - xo);
			ycoords[i] = (int)(verts[i].getY() - yo);
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
	private static ROVector2f[] centralized(ROVector2f[] in) {
		ROVector2f c = new Polygon(in).getCentroid();
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.sub(in[i], c);
		return out;
	}

	private static ROVector2f[] scaled(ROVector2f[] in, float scalefactor) {
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = MathUtil.scale(in[i], scalefactor);
		return out;
	}
}
