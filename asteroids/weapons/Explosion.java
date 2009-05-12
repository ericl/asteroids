/**
 * Weapons should leave explosion remnants.
 * Explosions are processed as a special case by Exploder.
 */

package asteroids.weapons;

import java.awt.Color;
import java.awt.Graphics2D;

import asteroids.display.*;

import net.phys2d.math.*;
import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

import static net.phys2d.math.MathUtil.*;

public abstract class Explosion extends PObj implements Drawable, Textured {
	private boolean tracking = true;
	private Vector2f d;
	private Body t;
	private TrackingMode mode;
	public enum TrackingMode {ORIGIN, TARGET, NONE};

	public Explosion(TrackingMode mode) {
		super(new Circle(1), 1);
		this.mode = mode;
		setEnabled(false);
	}

	public void setTracking(Body origin, Body target) {
		// ugly detection of weapon-on-weapon collisions
		if (target instanceof Weapon && origin instanceof Weapon)
			mode = TrackingMode.NONE;
		switch (mode) {
			case NONE:
				return;
			case ORIGIN:
				t = origin;
				break;
			case TARGET:
				t = target;
				break;
		}
		d = sub(getPosition(), t.getPosition());
	}

	public boolean preferDrawableFallback() {
		return false;
	}

	public Color getColor() {
		return Color.ORANGE;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public void endFrame() {
		super.endFrame();
		if (tracking && t != null)
			setPosition(t.getPosition().getX() + d.getX(), t.getPosition().getY() + d.getY());
	}

	/**
	 * @return	True if the explosion should be removed from the world.
	 */
	public abstract boolean dead();
}
