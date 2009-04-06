/**
 * Draws a small line that points towards another targets.
 */

package asteroids.handlers;
import java.awt.*;
import net.phys2d.math.*;
import static net.phys2d.math.MathUtil.*;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;

public class Pointer {
	private Display display;
	private Explodable ship;
	private Explodable[] targets;

	public Pointer(Explodable ship, Display display, Explodable ... targets) {
		this.ship = ship;
		this.targets = targets;
		this.display = display;
	}

	/**
	 * Updates position and draws to the screen.
	 */
	public void drawTo(Graphics2D g2d) {
		Vector2f o = sub(ship.getPosition(), scale(v(display.getDimension()), .5f));
		int length = Math.min(display.w(0),display.h(0))*9/20;
		for (Explodable target : targets) {
			g2d.setColor(target.getColor());
			if (ship.canExplode() || target.canExplode())
				break;
			if (display.inViewFrom(o, target.getPosition(), target.getRadius()))
				break;
			Vector2f delta = sub(target.getPosition(), ship.getPosition());
			double xo = display.w(0)/2;
			double yo = display.h(0)/2;
			double m = Math.sqrt(delta.length())/2;
			delta.normalise();
			g2d.drawLine((int)(xo - 5  + length*delta.getX()),
						 (int)(yo - 15 + length*delta.getY()),
						 (int)(xo - 5  + (length-m)*delta.getX()),
						 (int)(yo - 15 + (length-m)*delta.getY()));
		}
	}
}
