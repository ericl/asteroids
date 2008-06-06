package asteroids.bodies;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import java.util.*;

public abstract class Asteroid extends Body implements Explodable {
	public float damage;

	public void powerup(List<Body> list) {
		for (int i=1; i < Math.log10(getRadius()); i++)
			if (oneIn(1)) {
				list.add(PowerUp.random());
				return;
			}
	}

	public Asteroid(Polygon shape) {
		super(shape, shape.getArea());
	}

	public Asteroid(Circle shape) {
		super(shape, (float)Math.pow(shape.getRadius(),2));
	}

	public Asteroid(DynamicShape shape, float mass) {
		super(shape, mass);
	}

	/**
	 * Maximum visible radius of the asteroid.
	 */
	public abstract float getRadius();

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}

	public boolean canExplode() {
		return damage > Math.log10(getRadius()) / 5;
	}
}
