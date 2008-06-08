package asteroids.handlers;
import asteroids.*;
import asteroids.bodies.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.math.*;
import java.awt.*;
import asteroids.display.*;

/**
 * Draws a small line that points towards another ship.
 */

public class Pointer {
	private Display display;
	private Ship o;
	private Explodable target;
	private int rad;

	public Pointer(Ship t, Explodable x, Display d) {
		o = t;
		target = x;
		display = d;
	}

	/**
	 * Updates position and draws to the screen.
	 */
	public void drawTo(Graphics2D g2d) {
		if (target instanceof Ship)
			g2d.setColor(((Ship)target).statusColor());
		else
			g2d.setColor(AbstractGame.COLOR);
		rad = Math.min(display.w(0),display.h(0))*9/20;
		if (o.canExplode() || target.canExplode())
			return;
		Vector2f origin = sub(o.getPosition(), scale(v(display.getDimension()),.5f));
		if (display.inViewFrom(origin, target.getPosition(), target.getRadius()))
			return;
		Vector2f delta = sub(target.getPosition(), o.getPosition());
		double xo = display.w(0)/2;
		double yo = display.h(0)/2;
		double m = Math.sqrt(delta.length())/2;
		delta.normalise();
		g2d.drawLine((int)(xo - 5  + rad*delta.getX()),
					 (int)(yo - 15 + rad*delta.getY()),
					 (int)(xo - 5  + (rad-m)*delta.getX()),
					 (int)(yo - 15 + (rad-m)*delta.getY()));
	}
}
