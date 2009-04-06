/**
 * Common "visible" attributes used by almost all bodies in the game.
 */

package asteroids.display;

import java.awt.Color;

import net.phys2d.math.*;

public interface Visible {
	public Color getColor();

	/**
	 * Approximate mxa visible radius of body for visual calculations only.
	 * @return	The visible radius of the body.
	 */
	public float getRadius();

	/**
	 * The position of the body in the infinitely large world.
	 * @return	The position of the body.
	 */
	public ROVector2f getPosition();
}
