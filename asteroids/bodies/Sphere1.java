package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class Sphere1 extends CircleAsteroid implements Textured {
	private float sphereradius;

	public Sphere1(float radius) {
		super(radius);
		sphereradius = radius;
	}

	public Vector2f getTextureCenter() {
		return v(74,74);
	}

	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		if (getRadius() < 10)
			return f;
		for (int i=0; i < 4; i++)
			f.add(new Sphere1(getRadius() / 3));	
		float x = getPosition().getX();
		float y = getPosition().getY();
		float r = getRadius() / 3;
		f.get(0).setPosition(x + r, y - r);
		f.get(0).adjustVelocity(v(r,-r));
		f.get(1).setPosition(x - r, y - r);
		f.get(1).adjustVelocity(v(-r,-r));
		f.get(2).setPosition(x + r, y + r);
		f.get(2).adjustVelocity(v(+r,+r));
		f.get(3).setPosition(x - r, y + r);
		f.get(3).adjustVelocity(v(-r,+r));
		for (Body b : f) {
			b.addExcludedBody(this);
			addExcludedBody(b);
		}
		for (Body b : f)
			for (Body c : f)
				if (c != b) {
					c.addExcludedBody(b);
					b.addExcludedBody(c);
				}
		return f;
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
