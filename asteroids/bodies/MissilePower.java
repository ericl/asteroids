/**
 * 5 extra missiles.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class MissilePower extends PowerUp {
	protected static ROVector2f[] raw = {v(2,4),v(10,1),v(19,19),v(12,21),v(3,19),v(2,5),v(11,1)};

	public MissilePower() {
		super(raw, "pixmaps/edit-delete.png", 11, 11);
	}

	/**
	 * The rotation of the object is 0 because it does not spin.
	 * @return	The rotation number.
	 */
	public float getRotation() {
		return 0;
	}

	/**
	 * Upgrades the weapon of the ship.
	 * @param	ship	The Ship to receive the upgrade.
	 */
	protected void up(Ship ship) {
		ship.addMissiles(5);
	}
}
