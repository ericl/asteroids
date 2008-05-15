package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class Europa extends CircleAsteroid implements Textured {
	private float sphereradius;

	public Europa(float radius) {
		super(radius);
		sphereradius = radius;
	}

	public Vector2f getTextureCenter() {
		return v(150,150);
	}

	public String getTexturePath() {
		return "pixmaps/europa.png";
	}

	public float getTextureScaleFactor() {
		return sphereradius / 150.0f;
	}
	
	public float getRadius() {
		return sphereradius;
	}

	private static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}
}
