package asteroids.display;
import net.phys2d.math.*;

public interface Visible {
	/**
	 * @return The visible radius of the body.
	 */
	public float getRadius();
	public ROVector2f getPosition();
}
