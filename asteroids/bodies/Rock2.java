package asteroids.bodies;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Rock2 extends TexturedAsteroid {
	private static ROVector2f[] raw = {v(38,4),v(46,9),v(60,19),v(72,32),v(82,41),v(82,53),v(87,60),v(87,72),v(82,79),v(72,88),v(60,88),v(49,79),v(38,79),v(20,58),v(19,53),v(15,41),v(12,25),v(12,19),v(19,10),v(36,4)};

	protected boolean explode;

	public Rock2(float radius) {
		super(raw, "pixmaps/rock2.png", 40, radius);
	}

	public Body getRemnant() {
		return getRadius() > 20 ? new Rock2(getRadius() * 2 / 3) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(5);
		int max = (int)range(3,4);
		if (getRadius() > 10)
			for (int i=0; i < max; i++)
				f.add(new Rock1(getRadius() / 3));	
		return f;
	}
}
