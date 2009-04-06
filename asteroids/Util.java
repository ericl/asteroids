/**
 * Common operation shortcuts to avoid cluttering the code.
 */

package asteroids;
import java.awt.*;
import net.phys2d.math.*;

public class Util {
	private Util() {
		// prevent construction
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

	public static Dimension d(Vector2f d) {
		return d(d.getX(), d.getY());
	}

	public static float range(Number minR, Number maxR) {
		float min = minR.floatValue();
		float max = maxR.floatValue();
		return (float)(min+(max-min)*Math.random());
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
