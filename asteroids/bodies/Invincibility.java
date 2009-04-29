/**
 * Grants invincibility to the ship for a short period.
 */

package asteroids.bodies;

import net.phys2d.math.ROVector2f;

import static asteroids.Util.*;

public class Invincibility extends PowerUp {
	private static ROVector2f[] raw = { v(111,23), v(145,78), v(205,90), v(162,137), v(171,199), v(111,174), v(54,201), v(62,139), v(19,92), v(79,78)};
	private static int INVINCIBLE_TIME = 20000, WARNING_TIME = 4000;
	private boolean canExplode = true;

	public Invincibility() {
		super(raw, "pixmaps/invincibility.png", 112.5f, 11.25f);
		setRotDamping(1);
	}

	public boolean canExplode() {
		return canExplode && super.canExplode();
	}

	/**
	 * @param	ship	Ship that is to be made invincible.
	 */
	protected void up(Enhancable ship) {
		ship.gainInvincibility(INVINCIBLE_TIME, WARNING_TIME);
	}
}
