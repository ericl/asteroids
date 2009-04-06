/**
 * Smallest of the rocky asteroids.
 * All rocky asteroid textures were created by Will.
 */

package asteroids.bodies;
import java.util.*;
import net.phys2d.raw.Body;
import net.phys2d.math.ROVector2f;
import static asteroids.Util.v;

public class SmallAsteroid extends TexturedAsteroid {
	private static ROVector2f[] raw = {v(32,2), v(45,1), v(54,9), v(55,18), v(57,21), v(58,32), v(46,50), v(39,56), v(32,56), v(19,62), v(9,54), v(7,46), v(1,39), v(6,24), v(25,8)};
	
	public SmallAsteroid(float size) {
		super(raw, "pixmaps/3.png", 35, size);
	}

	public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		SmallAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new SmallAsteroid(getRadius() / 3);
				f.add(tmp);
			}
		return f;
	}
}
