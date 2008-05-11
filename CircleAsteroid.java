import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class CircleAsteroid extends Asteroid implements Drawable {

	public CircleAsteroid(Circle circle) {
		super(circle, (float)Math.pow(circle.getRadius(),2));
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

	public static CircleAsteroid random(int minR, int maxR) {
		Circle circle = new Circle((float)(minR+(maxR-minR)*Math.random()));
		return new CircleAsteroid(circle);	
	}
}
