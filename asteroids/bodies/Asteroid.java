/**
 * Basic functions of an asteroid.
 */

package asteroids.bodies;

import asteroids.display.*;

import java.util.*;

import net.phys2d.raw.*;

public interface Asteroid extends Explodable, Drawable {
	public static int MIN_SIZE = (int)Math.sqrt(10), BASE_CHANCE = 12;
	public void addPowerups(List<Body> list);
}
