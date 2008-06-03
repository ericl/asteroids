/**
 * 	An asteroid...
 */

package asteroids.bodies;
import asteroids.handlers.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Asteroid extends Body implements Explodable {
	public float damage;

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
