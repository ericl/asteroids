package asteroids.handlers;
import java.awt.*;
import net.phys2d.math.*;
import asteroids.display.*;
import java.util.*;
import static asteroids.Util.*;

public class StarField {
	private final static int MIN_DENSITY = 300, BORDER = 200, BUF = 300;
	private LinkedList<Star> stars = new LinkedList<Star>();
	private Display display;
	private Visible[] targets;
	private int[] density;

	public StarField(Visible t, Display d) {
		targets = new Visible[1];
		density = new int[1];
		targets[0] = t;
		display = d;
	}

	public StarField(Visible[] t, Display d) {
		targets = t;
		density = new int[targets.length];
		display = d;
	}

	public void init() {
		synchronized (stars) {
			stars.clear();
			for (int i=0; i < density.length; i++)
				density[i] = 0;
			ROVector2f tmp;
			for (int i=0; i < density.length; i++)
				while (density[i] < MIN_DENSITY) {
					stars.add(new Star(tmp = display.getRandomCoords(
						BORDER, targets[i].getPosition())));
					for (int j=0; j < targets.length; j++) {
						if (display.inViewFrom(
								tmp, targets[j].getPosition(), BORDER+BUF))
							density[j]++;
					}
				}
		}
	}
	
	public void starField() {
		synchronized (stars) {
			ListIterator<Star> iter = stars.listIterator();
			for (int i=0; i < density.length; i++)
				density[i] = 0;
			while (iter.hasNext()) {
				Star star = iter.next();
				for (int i=0; i < targets.length; i++) {
					if (display.inViewFrom(
							star, targets[i].getPosition(), BORDER+BUF))
						density[i]++;
				}
				if (!display.inView(star, BORDER+BUF))
					iter.remove();
			}
			ROVector2f tmp;
			for (int i=0; i < density.length; i++)
				while (density[i] < MIN_DENSITY) {
					stars.add(new Star(tmp = display.getOffscreenCoords(
						Star.MAX_SIZE, BORDER, targets[i].getPosition())));
					for (int j=0; j < targets.length; j++) {
						if (display.inViewFrom(
								tmp, targets[j].getPosition(), BORDER+BUF))
							density[j]++;
					}
				}
		for (Star star : stars)
			display.drawDrawable(star);
		}
	}
}

class Star extends Vector2f implements Drawable {
	public final static int MIN_SIZE = 1;
	public final static int MAX_SIZE = 4;
	private static Color[] colors = {Color.yellow,Color.orange,Color.cyan};
	private int radius = (int)range(MIN_SIZE,MAX_SIZE);
	private Color color = starColor();

	public Star(ROVector2f v) {
		super(v);
	}

	public float getRadius() {
		return radius;
	}

	public void drawTo(Graphics2D g2d, ROVector2f origin) {
		Vector2f tmp = MathUtil.sub(this, origin);
		int disp = (int)(radius/2);
		g2d.setColor(color);
		g2d.fillOval((int)tmp.getX()-disp, (int)tmp.getY()-disp, radius, radius);
	}

	public Vector2f getPosition() {
		return this;
	}

	private static Color starColor() {
		Color c = oneIn(5) ? Color.WHITE : Color.GRAY;
		if (oneIn(10))
			c = colors[(int)range(0,colors.length)];
		return c;
	}
}
