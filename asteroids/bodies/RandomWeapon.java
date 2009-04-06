/**
 * Powerup that interacts with the weapon systems of the ship.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import asteroids.display.*;

import static asteroids.Util.*;

/**
 * Powerup that interacts with the weapon systems of the ship.
 */
public class RandomWeapon extends PowerUp implements Textured {
	protected static ROVector2f[] raw = {v(11,1),v(20,10),v(11,20),v(1,11)};

	public RandomWeapon() {
		super(raw, "pixmaps/dialog-question.png", 11, 11);
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
		ship.weapons.setRandomWeaponType();
	}
}
