/**
 * Large rocky asteroid that explodes into MediumAsteroids.
 */

package asteroids.bodies;
import java.util.*;
import net.phys2d.raw.Body;
import net.phys2d.math.ROVector2f;
import static asteroids.Util.*;

public class BigAsteroid extends TexturedAsteroid {
	// a bit too detailed here...
	private static ROVector2f[] raw = {v(97,1), v(108,3), v(113,8), v(149,14), v(149,18), v(156,23), v(160,28), v(177,35), v(184,53), v(180,58), v(189,72),
		v(189,79), v(186,82), v(188,89), v(188,101), v(179,109), v(181,119), v(169,131), v(169,136), v(163,141), v(157,152), v(157,158), v(131,166), v(93,168),
		v(72,164), v(49,152), v(16,121), v(15,112), v(11,100), v(14,100), v(11,100), v(16,89), v(11,75), v(22,56), v(22,51), v(32,38), v(27,27), v(37,17)};
	
	public BigAsteroid(float size) {
		super(raw, "pixmaps/1.png", 90, size);
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new MediumAsteroid(getRadius() / 2) : null;
	}

	public String getCause() {
		return "an asteroid";
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		SmallAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new SmallAsteroid(getRadius() / 3);
				f.add(tmp);
			}
		addPowerups(f);
		return f;
	}
}
