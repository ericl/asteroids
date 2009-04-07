/**
 * Powerup that interacts with the weapon systems of the ship.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class WeaponPower extends PowerUp {
	protected static ROVector2f[] raw = {v(3,3),v(12,2),v(21,8),v(18,12),v(19,19),v(3,21),v(6,14),v(0,9)};

	public WeaponPower() {
		super(raw, "pixmaps/weather-storm.png", 11, 11);
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
	protected void up(Enhancable ship) {
		ship.upgradeWeapons();
	}
}
