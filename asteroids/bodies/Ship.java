/**
 * User-controlled ship in the world.
 */

package asteroids.bodies;

import static asteroids.Util.*;

import asteroids.weapons.Laser2;

import net.phys2d.math.*;

import net.phys2d.raw.*;

public class Ship extends Entity {
	protected static ROVector2f[] raw = {v(31,4), v(35,8), v(37,16), v(38,23), v(37,29), v(40,28), v(55,28), v(55,32), v(41,40), v(37,36), v(38,45), v(24,45), v(24,36), v(22,40), v(8,33), v(8,28), v(23,28), v(27,30), v(26,24), v(26,16), v(28,7)};
	protected int thrust;

	public Ship(World world) {
		super(raw, "pixmaps/ship.png", 64, 44, 1500, world, null);
	}

	public Ship(World world, boolean shieldOn) {
		super(raw, "pixmaps/ship.png", 64, 44, 1500, world, null);
		defaultShield = raiseShield = shieldOn;
	}

	protected float getMaxArmor() {
		return 3;
	}

	public int getPointValue() {
		return 100;
	}

	public void reset() {
		super.reset();
		weapons.setWeaponType(new Laser2());
	}

	public String getTexturePath() {
		if (!canTarget())
			return "pixmaps/ship-c.png";
		return thrust > 0 ? "pixmaps/ship-t.png" : "pixmaps/ship.png";
	}

	public void endFrame() {
		super.endFrame();
		thrust--;
	}

	protected void accel() {
		if (accel > 0)
			thrust = 5;
		super.accel();
	}
}
