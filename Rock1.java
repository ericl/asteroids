import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class Rock1 extends PolyAsteroid implements Textured {
	private static ROVector2f[] raw = {v(4,16),v(22,3),v(30,2),v(36,3),v(48,16),v(48,23),v(46,26),v(36,31),v(30,34),v(22,34),v(13,34),v(4,26),v(3,23)};
	private static Vector2f centroid = new Polygon(raw).getCentroid();
	private static Polygon poly = new Polygon(centralized(raw));

	public Rock1() {
		super(poly);
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}

	public String getTexturePath() {
		return "pixmaps/rock1.png";
	}

	public float getTextureScaleFactor() {
		return 1.0f;
	}
	
	public float getRadius() {
		return 36;
	}

	private static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}
}
