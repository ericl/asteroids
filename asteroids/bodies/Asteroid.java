/**
 * 	An asteroid...
 */

package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Asteroid extends Body
		implements Visible, Comparable<Asteroid>, Explodable {

	protected long gid = -1;

	public void setGID(long id) {
		gid = id;
	}

	public long getGID() {
		if (gid < 0)
			gid = System.nanoTime();
		return gid;
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

	public int compareTo(Asteroid other) {
		return (int)(other.getRadius() - getRadius());
	}
}
