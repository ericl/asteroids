package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.util.*;

public interface Explodable extends Visible {

	/**
	 * @return True if the body should be exploded.
	 */
	public boolean canExplode();

	/**
	 * @return Fragments from exploding the body.
	 */
	public List<Body> explode();

	/**
	 * Notification that the explodable object has been hit.
	 */
	public void collided(CollisionEvent event);
}
