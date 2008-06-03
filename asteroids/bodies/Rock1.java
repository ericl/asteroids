package asteroids.bodies;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Rock1 extends TexturedAsteroid {
	private static ROVector2f[] raw = {v(4,16),v(22,3),v(30,2),v(36,3),v(48,16),v(48,23),v(46,26),v(36,31),v(30,34),v(22,34),v(13,34),v(4,26),v(3,23)};

	public Rock1(float radius) {
		super(raw, "pixmaps/rock1.png", 20, radius);
	}

	public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		return null;
	}
}
