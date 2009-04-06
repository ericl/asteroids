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

public abstract class Explosion extends Body implements Drawable, Textured {

	public Explosion() {
		super(new Circle(1), 1);
		setEnabled(false);
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

	/**
	 * @return	True if the explosion should be removed from the world.
	 */
	public abstract boolean dead();
}
