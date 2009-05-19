/**
 * Draws a small line that points towards another targets.
 */

package asteroids.handlers;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

import asteroids.bodies.*;

import asteroids.display.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

public class Radar {
	private World world;
	private Display display;
	private Explodable ship;

	public Radar(Explodable ship, Display display, World world) {
		this.ship = ship;
		this.display = display;
		this.world = world;
	}

	private List<Entity> getTargets() {
		List<Entity> targets = new ArrayList<Entity>();
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b instanceof Entity && !b.equals(ship))
				targets.add((Entity)b);
		}
		return targets;
	}

	/**
	 * Updates position and draws to the screen.
	 */
	public void drawTo(Graphics2D g2d) {
		Vector2f o = sub(ship.getPosition(), scale(v(display.getDimension()), .5f));
		int length = Math.min(display.w(0),display.h(0))*9/20;
		for (Explodable target : getTargets()) {
			if (display.inViewFrom(o, target.getPosition(), target.getRadius()))
				continue;
			g2d.setColor(Color.CYAN);
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
