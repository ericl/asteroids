package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class Sphere1 extends CircleAsteroid implements Textured {
	private float sphereradius;

	public Sphere1(float radius) {
		super(radius);
		sphereradius = radius;
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
}
