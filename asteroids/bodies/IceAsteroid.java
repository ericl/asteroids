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

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Circle circle = (Circle)getShape();
		g2d.setColor(Color.CYAN);
		float x = getPosition().getX() - xo;
		float y = getPosition().getY() - yo;
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public Body getRemnant() {
		return getRadius() > 10 ? new IceAsteroid(getRadius() - 5) : null;
	}

	public List<Body> getFragments() {
		return null;
	}
}
