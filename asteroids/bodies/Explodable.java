package asteroids.bodies;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.util.*;

public interface Explodable {

	/**
	 * @return True if the body should be exploded.
	 */
	public boolean canExplode();

	/**
	 * @return Fragments from exploding the body.
	 */
	public List<Body> explode();

	// note the body method 'void collided(Body other)'
	public void collided(CollisionEvent event);
}
