package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class ArmorRecovery extends PowerUp implements Drawable {

	public ArmorRecovery() {
		super(new Circle(5));
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(Color.green);
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public float getRadius() {
		return ((Circle)getShape()).getRadius();
	}
	
	public void up(Ship ship) {
		ship.setArmor(Ship.MAX);
	}
	
	public boolean canExplode() {
		return true;
	}
}
