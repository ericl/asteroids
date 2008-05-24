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

	public static Vector2f direction(Number rotation) {
		return v(Math.sin(rotation.doubleValue()),
		        -Math.cos(rotation.doubleValue()));
	}

	public static boolean oneIn(int num) {
		return num*Math.random() < 1;
	}

	/**
	 * @param o The center of the screen.
	 * @param dim The dimensions of the screen.
	 * @param v The absolute location of the object.
	 * @param r The visible radius of the object.
	 */
	public static boolean isVisible(ROVector2f o, ROVector2f dim,
			ROVector2f v, float r) {
		Vector2f rel = MathUtil.sub(v, o);
		return rel.getX() > -r && rel.getX() < dim.getX()+r
			&& rel.getY() > -r && rel.getY() < dim.getY()+r;
	}
}
