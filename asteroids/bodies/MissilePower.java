/**
 * 5 extra missiles.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class MissilePower extends PowerUp {
	protected static ROVector2f[] raw = {v(6,6),v(12,5),v(18,12),v(11,18),v(5,14)};

	public MissilePower() {
		super(raw, "pixmaps/edit-delete.png", 11, 11);
	}

	/**
	 * Upgrades the weapon of the ship.
	 * @param	ship	The Ship to receive the upgrade.
	 */
	protected void up(Enhancable ship) {
		ship.addMissiles(5);
	}
}
