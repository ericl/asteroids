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
	private Color color = Color.orange;

	public CircleAsteroid(float radius) {
		super(new Circle(radius), (float)Math.pow(radius,2));
	}

	public CircleAsteroid(float radius, float fixedmass) {
		super(new Circle(radius), fixedmass);
	}

	public void setColor(Color c) {
		color = c;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(getRadius() < 100 ? color : Color.darkGray);
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
		CircleAsteroid x = new CircleAsteroid(getRadius() / 2);
		x.setColor(color);
		return getRadius() > 15 ? x : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(4);
		int max = (int)(getRadius() > 30 ? range(4,6) : range(3,4));
		CircleAsteroid x = null;
		if (getRadius() > 10)
			for (int i=0; i < max; i++) {
				f.add(x = new CircleAsteroid(getRadius() / 3));
				x.setColor(color);
			}
		return f;
	}
}
