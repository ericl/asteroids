package asteroids.bodies;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import java.util.*;

public abstract class Asteroid extends Body implements Explodable {
	protected float damage;
	private static int MIN_SIZE = (int)Math.sqrt(10);
	private static int BASE_CHANCE = 7;

	public void powerup(List<Body> list) {
		for (int i = MIN_SIZE; i < Math.sqrt(getRadius()); i++)
			if (oneIn(BASE_CHANCE)) {
				list.add(PowerUp.random());
				return;
			}
	}

	public Asteroid(Polygon shape) {
		super(shape, shape.getArea());
		setRestitution(.5f);
	}

	public Asteroid(Circle shape) {
		super(shape, (float)Math.pow(shape.getRadius(),2));
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
