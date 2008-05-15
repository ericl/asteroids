package asteroids.bodies;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.util.*;

// note the body method 'void collided(Body other)'

public interface Explodable {

	/**
	 * @return True if the body should be exploded.
	 */
	public boolean canExplode();

	/**
	 * @return Fragments from exploding the body, each speeding outwards.
	 */
	public List<Body> explode();
}
