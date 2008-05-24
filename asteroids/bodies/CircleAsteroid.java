package asteroids.bodies;
import static asteroids.Util.*;
import asteroids.display.*;
import asteroids.handlers.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class CircleAsteroid extends Asteroid implements Drawable {
	protected boolean explode;

	public CircleAsteroid(float radius) {
		super(new Circle(radius), (float)Math.pow(radius,2));
	}

	public CircleAsteroid(float radius, float fixedmass) {
		super(new Circle(radius), fixedmass);
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(getRadius() < 100 ? Color.ORANGE : Color.darkGray);
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public float getRadius() {
		return ((Circle)getShape()).getRadius();
	}

	public void collided(CollisionEvent event) {
		if (Exploder.worthyCollision(event))
			explode = true;
	}

	public boolean canExplode() {
		return getRadius() < 100 ? explode : explode ? oneIn(10) : false;
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new CircleAsteroid(getRadius() / 2) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(4);
		int max = (int)(getRadius() > 30 ? range(4,6) : range(3,4));
		if (getRadius() > 10)
			for (int i=0; i < max; i++)
				f.add(new CircleAsteroid(getRadius() / 3));	
		return f;
	}
}
