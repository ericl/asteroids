/**
 * Boring circle asteroids not directly used in the game.
 */

package asteroids.bodies;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics2D;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.handlers.Exploder;
import static asteroids.Util.*;

public class CircleAsteroid extends PObj implements Asteroid {
	protected float damage;
	protected Color color = Color.ORANGE;

	public CircleAsteroid(float radius) {
		super(new Circle(radius), radius*radius);
	}

	public CircleAsteroid(float radius, float mass) {
		super(new Circle(radius), mass);
	}

	public Color getColor() {
		return getRadius() < 100 ? color : Color.darkGray;
	}

	public void setColor(Color c) {
		color = c;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public float getRadius() {
		return ((Circle)getShape()).getRadius();
	}

	public boolean canExplode() {
		if (getRadius() >= 100)
			return damage > getRadius() / 7; // these are not your normal asteroids!
		return damage > Math.log10(getRadius()) / 5;
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
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
		addPowerups(f);
		return f;
	}

	public void addPowerups(List<Body> list) {
		for (int i = MIN_SIZE; i < Math.sqrt(getRadius()); i++)
			if (oneIn(BASE_CHANCE)) {
				list.add(PowerUp.random());
				return;
			}
	}
}
