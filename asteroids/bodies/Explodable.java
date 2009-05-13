/**
 * Interface for explosion handling by Exploder.
 */

package asteroids.bodies;
import java.util.*;
import net.phys2d.raw.*;
import asteroids.display.*;

/**
 * Interface for explosion handling by Exploder.
 */
public interface Explodable extends Visible, Body {
	/**
	 * @return	True if the body should be exploded or has exploded.
	 * Should be callable at any time.
	 */
	public boolean canExplode();

	/**
	 * @return	Fragments from exploding the body.
	 */
	public List<Body> getFragments();

	/**
	 * @return	Largest fragment of the body, null if none.
	 */
	public Body getRemnant();

	/**
	 * Notification that the explodable object has been hit.
	 * This is guaranteed to be called before getFragment or getRemnant.
	 */
	public void collided(CollisionEvent event);
}
