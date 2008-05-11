import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public abstract class PolyAsteroid extends Asteroid implements Drawable {

	protected PolyAsteroid(Polygon poly) {
		super(poly, 1000f);
	}

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Polygon poly = (Polygon)getShape();
		g2d.setColor(Color.blue);
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - xo);
			ycoords[i] = (int)(verts[i].getY() - yo);
		}
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	protected static ROVector2f[] centralized(ROVector2f[] in) {
		ROVector2f c = new Polygon(in).getCentroid();
		ROVector2f[] out = new ROVector2f[in.length];
		for (int i=0; i < in.length; i++)
			out[i] = new Vector2f(in[i].getX()-c.getX(),in[i].getY()-c.getY());
		return out;
	}
}
