/**
 * 	An asteroid...
 */

import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Asteroid extends Body {
	public Asteroid(DynamicShape shape, float m) {
		super(shape, m);
	}

	/**
	 * Maximum visible radius of the asteroid.
	 */
	public abstract float getRadius();
}
