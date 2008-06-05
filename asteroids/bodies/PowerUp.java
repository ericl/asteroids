package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class PowerUp extends Body
		implements Explodable{

	public PowerUp(Polygon shape) {
		super(shape, shape.getArea());
	}

	public PowerUp(Circle shape) {
		super(shape, (float)Math.pow(shape.getRadius(),2));
	}

	public PowerUp(DynamicShape shape, float mass) {
		super(shape, mass);
	}

	/**
	 * Maximum visible radius of the asteroid.
	 */
	public abstract float getRadius();
}