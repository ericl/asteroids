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

public class IceAsteroid extends CircleAsteroid {

	public IceAsteroid(float radius) {
		super(radius);
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(Color.CYAN);
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public void collided(CollisionEvent e) {
		setShape(new Circle(getRadius() - 5));
	}

	public boolean canExplode() {
		return getRadius() < 5;
	}

	public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		return null;
	}
}
