package asteroids.bodies;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Rock1 extends TexturedAsteroid {
	//private static ROVector2f[] raw = {v(4,16),v(22,3),v(30,2),v(36,3),v(48,16),v(48,23),v(46,26),v(36,31),v(30,34),v(22,34),v(13,34),v(4,26),v(3,23)};
	private static ROVector2f[] raw = {v(97,1), v(108,3), v(113,8), v(149,14), v(149,18), v(156,23), v(160,28), v(177,35), v(184,53), v(180,58), v(189,72),
		v(189,79), v(186,82), v(188,89), v(188,101), v(179,109), v(181,119), v(169,131), v(169,136), v(163,141), v(157,152), v(157,158), v(131,166), v(93,168),
		v(72,164), v(49,152), v(16,121), v(15,112), v(11,100), v(14,100), v(11,100), v(16,89), v(11,75), v(22,56), v(22,51), v(32,38), v(27,27), v(37,17)};
	
	
	public Rock1(float radius) {
		super(raw, "pixmaps/aster1.png", 90, radius);
	}

	public Body getRemnant() {
		return null;
	}
	
	public List<Body> getFragments() {
		return null;
	}
}
