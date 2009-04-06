/**
 * Wrap around starfield that provides visual consistency at
 * the expense of speed (usually < 1ms)
 */

package asteroids.handlers;

import java.awt.*;
import java.awt.Toolkit;

import java.util.*;

import asteroids.display.*;

import net.phys2d.math.*;

import static net.phys2d.math.MathUtil.*;

import static asteroids.Util.*;

public class StarField {
	// hoping that no one has really high resolution monitors
	private static int DIMENSION = 2*maxRes();
	private static double DENSITY = 2e-4;
	private static Color[] colors = {Color.yellow,Color.orange,Color.cyan};
	private LinkedList<Star> stars = new LinkedList<Star>();
	private Display display;

	public StarField(Display d) {
		display = d;
	}

	public static int maxRes() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		return (int)Math.max(screen.getWidth(), screen.getHeight());
	}

	/**
	 * Creates stars and adds them to the linked list of stars.
	 */
	public void init() {
		int numstars = (int)(DIMENSION*DIMENSION*DENSITY);
		stars.clear();
		for (int i=0; i < numstars; i++)
			stars.add(new Star((float)Math.random(), v(range(0,DIMENSION), range(0,DIMENSION))));
	}
	
	/**
	 * Draws the stars on the window screen.
	 */
	public void starField() {
		for (Star star : stars)
			display.drawDrawable(star);
	}

	
	/**
	 * @return	A random star color.
	 */
	private static Color starColor() {
		Color c = oneIn(5) ? Color.WHITE : Color.GRAY;
		if (oneIn(10))
			c = colors[(int)range(0,colors.length)];
		return c;
	}

	/**
	 * Constructs a star.
	 */
	private class Star implements Drawable {
		public final static int MIN_SIZE = 1;
		public final static int MAX_SIZE = 4;
		protected Vector2f loc;
		protected float scaler;
		protected int radius = (int)range(MIN_SIZE,MAX_SIZE);
		protected Color color = starColor();

		public Color getColor() {
			return color;
		}

		public Star(float s, Vector2f v) {
			loc = v;
			scaler = s;
		}

		public float getRadius() {
			// trick the display into letting us handle clipping
			return Float.POSITIVE_INFINITY;
		}

		public void drawTo(Graphics2D g2d, ROVector2f origin) {
			origin = scale(origin, scaler);
			// wrap around the display
			while (loc.getX() - origin.getX() > DIMENSION)
				loc = MathUtil.sub(loc, v(DIMENSION,0));
			while (loc.getX() - origin.getX() < 0)
				loc = MathUtil.sub(loc, v(-DIMENSION,0));
			while (loc.getY() - origin.getY() > DIMENSION)
				loc = MathUtil.sub(loc, v(0,DIMENSION));
			while (loc.getY() - origin.getY() < 0)
				loc = MathUtil.sub(loc, v(0,-DIMENSION));
			if (display.inView(loc, radius, origin)) {
				Vector2f tmp = MathUtil.sub(loc, origin);
				g2d.setColor(color);
				g2d.fillOval((int)tmp.getX(), (int)tmp.getY(), radius, radius);
			}
		}

		public Vector2f getPosition() {
			return loc;
		}
	}
}
