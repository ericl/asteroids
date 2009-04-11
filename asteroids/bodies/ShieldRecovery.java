/**
 * Powerup repairing ship armor.
 */

package asteroids.bodies;

import static asteroids.Util.*;

import net.phys2d.math.*;

public class ShieldRecovery extends PowerUp {
	private static ROVector2f[] raw = { v(176,8), v(325,30), v(311,54), v(307,78), v(310,113), v(310,138), v(306,167), v(281,212), v(176,329), v(56,186), v(44,159), v(41,140), v(41,115), v(44,80), v(40,53), v(28,28), v(86,16) };

	/**
	 * Constructs the ShieldRecovery Powerup.
	 */
	public ShieldRecovery() {
		super(raw, "pixmaps/shield2.png", 175, 8.75f);
	}
	
	/**
	 * Rotation number is 0 because it does not spin.
	 * @return	The rotation number.
	 */
	public float getRotation() {
		return 0;
	}

	/**
	 * Changes the armor of the ship to 100.
	 * @param	ship	The Ship being powered up.
	 */
	protected void up(Enhancable ship) {
		ship.raiseShields();
	}
}
