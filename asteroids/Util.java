/**
 * Common operation shortcuts to avoid cluttering the code.
 */

package asteroids;
import java.awt.*;
import net.phys2d.math.*;

public class Util {
	public static final long BIT_SHIELD_PENETRATING = 1l;
	public static final long BIT_MIN_FREE = 2l;
	private static String id;
	private static long time;

	private Util() {
		// prevent construction
	}

	public static void mark(String id) {
		Util.id = id;
		time = System.nanoTime();
	}

	public static void report() {
		System.out.println(((System.nanoTime() - time) / 1e6) + "ms @ " + id);
	}

	public static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}

	public static Dimension d(Number x, Number y) {
		return new Dimension(x.intValue(), y.intValue());
	}

	public static Vector2f v(Dimension d) {
		return v(d.getWidth(), d.getHeight());
	}

	public static Vector2f v(Point p) {
		return v(p.getX(), p.getY());
	}

	public static Dimension d(Vector2f d) {
		return d(d.getX(), d.getY());
	}

	public static float range(Number minR, Number maxR) {
		float min = minR.floatValue();
		float max = maxR.floatValue();
		return (float)(min+(max-min)*Math.random());
	}

	public static Vector2f negate(ROVector2f r) {
		return v(-r.getX(), -r.getY());
	}

	public static Vector2f direction(Number rotation) {
		return v(Math.sin(rotation.doubleValue()),
		        -Math.cos(rotation.doubleValue()));
	}

	public static boolean oneIn(int num) {
		return num*Math.random() < 1;
	}

	public static Color randomColor() {
		return new Color((int)range(1,255),(int)range(1,255),(int)range(1,255));
	}
}
