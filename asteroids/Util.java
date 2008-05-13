package asteroids;
import net.phys2d.math.*;

public class Util {
	private Util() {
		// prevent construction
	}

	public static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}

	public static float range(float minR, float maxR) {
		return (float)(minR+(maxR-minR)*Math.random());
	}
}
