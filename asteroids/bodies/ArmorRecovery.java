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

public class ArmorRecovery extends PowerUp implements Drawable {
	private Color color = Color.green;

	public ArmorRecovery(float radius) {
		super(new Circle(radius), (float)Math.pow(radius,2));
		setMoveable(false);
	}
	
	public void collided(CollisionEvent e) {
		Body a = e.getBodyA();
		Body b = e.getBodyB();
		if(a instanceof Ship)
		{
			up((Ship)a);
		}
		if(b instanceof Ship)
		{
			up((Ship)b);
		}
	}

	public ArmorRecovery(float radius, float fixedmass) {
		super(new Circle(radius), fixedmass);
		setMoveable(false);
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
	
	public void up(Ship ship)
	{
		ship.setArmor(1);
	}
	
		public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		return null;
	}
	
		public boolean canExplode() {
		return true;
	}
}
