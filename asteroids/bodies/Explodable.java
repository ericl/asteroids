package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import java.util.*;

public interface Explodable extends Visible {

	/**
	 * @return True if the body should be exploded.
	 */
	public boolean canExplode();

	/**
	 * @return Fragments from exploding the body.
	 */
	public List<Body> getFragments();

	/**
	 * @return Largest fragment of the body, null if none.
	 */
	public Body getRemnant();

	/**
	 * Notification that the explodable object has been hit.
	 */
	public void collided(CollisionEvent event);

	public long getGID();
	public void setGID(long id);
}
