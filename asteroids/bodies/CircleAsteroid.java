package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class CircleAsteroid extends Asteroid implements Drawable {

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
}
