package asteroids.handlers;
import java.awt.*;
import net.phys2d.math.*;
import asteroids.display.*;
import java.util.*;
import static asteroids.Util.*;

/**
 * Wrap around starfield that provides visual consistency at
 * the expense of speed (usually < 1ms)
 */
public class FiniteStarField {
	// hoping that no one has really high resolution monitors
	private static int DIMENSION = 3000;
	private static double DENSITY = 2e-4;
	private static Color[] colors = {Color.yellow,Color.orange,Color.cyan};
	private LinkedList<Star> stars = new LinkedList<Star>();
	private Display display;

	public FiniteStarField(Display d) {
		display = d;
	}

	public void init() {
		int numstars = (int)(DIMENSION*DIMENSION*DENSITY);
		synchronized (stars) {
			stars.clear();
			for (int i=0; i < numstars; i++)
				stars.add(new Star(v(range(0,DIMENSION), range(0,DIMENSION))));
		}
	}
	
	public void starField() {
		synchronized (stars) {
			for (Star star : stars)
				display.drawDrawable(star);
		}
	}

	private static Color starColor() {
		Color c = oneIn(5) ? Color.WHITE : Color.GRAY;
		if (oneIn(10))
			c = colors[(int)range(0,colors.length)];
		return c;
	}

	private class Star implements Drawable {
		public final static int MIN_SIZE = 1;
		public final static int MAX_SIZE = 4;
		private Vector2f loc;
		private int radius = (int)range(MIN_SIZE,MAX_SIZE);
		private Color color = starColor();

		public Star(Vector2f v) {
			loc = v;
		}

		public float getRadius() {
			// XXX trick the display
			return Float.POSITIVE_INFINITY;
		}

		public void drawTo(Graphics2D g2d, ROVector2f origin) {
			// wrap around the display
			while (loc.getX() - origin.getX() > DIMENSION)
				loc = MathUtil.sub(loc, v(DIMENSION,0));
			while (loc.getX() - origin.getX() < 0)
				loc = MathUtil.sub(loc, v(-DIMENSION,0));
			while (loc.getY() - origin.getY() > DIMENSION)
				loc = MathUtil.sub(loc, v(0,DIMENSION));
			while (loc.getY() - origin.getY() < 0)
				loc = MathUtil.sub(loc, v(0,-DIMENSION));
			if (display.inView(loc,radius)) {
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
