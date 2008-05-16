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

public class CircleAsteroid extends Asteroid implements Drawable, Explodable {
	protected boolean explode;

	public CircleAsteroid(float radius) {
		super(new Circle(radius), (float)Math.pow(radius,2));
	}

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Circle circle = (Circle)getShape();
		g2d.setColor(getRadius() < 100 ? Color.ORANGE : Color.darkGray);
		float x = getPosition().getX() - xo;
		float y = getPosition().getY() - yo;
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
		return explode;
	}

	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		if (getRadius() > 10)
			for (int i=0; i < 4; i++)
				f.add(new CircleAsteroid(getRadius() / 3));	
		return f;
	}
}
