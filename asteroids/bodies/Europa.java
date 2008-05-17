package asteroids.bodies;
import asteroids.handlers.*;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class Europa extends CircleAsteroid implements Textured {
	private float sphereradius;
	private int explode = 0;

	public Europa(float radius) {
		super(radius, Body.INFINITE_MASS);
		sphereradius = radius;
	}

	public Vector2f getTextureCenter() {
		return v(150,150);
	}

	public void collided(CollisionEvent event) {
		if (Exploder.worthyCollision(event))
			explode++;
	}

	public boolean canExplode() {
		return explode > 50+5000*Math.random();
	}

	public String getTexturePath() {
		return "pixmaps/europa.png";
	}

	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		if (getRadius() > 10)
			for (int i=0; i < 6; i++)
				f.add(new HexAsteroid(50));	
		return f;
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
