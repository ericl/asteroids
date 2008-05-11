import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class Sphere1 extends CircleAsteroid implements Textured {
	private float sphereradius;

	private Sphere1(Circle c) {
		super(c);
		sphereradius = c.getRadius();
	}

	public Vector2f getTextureCenter() {
		return v(74,74);
	}

	public String getTexturePath() {
		return "pixmaps/circle1.png";
	}

	public float getTextureScaleFactor() {
		return sphereradius / 41.0f;
	}
	
	public float getRadius() {
		return sphereradius * 1.25f;
	}

	private static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}

	public static Sphere1 random(int minR, int maxR) {
		Circle circle = new Circle((float)(minR+(maxR-minR)*Math.random()));
		return new Sphere1(circle);	
	}
}
