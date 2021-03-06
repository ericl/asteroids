package asteroids.bodies;

import static asteroids.Util.*;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

public class Frigate extends AbstractEntity {
	protected static ROVector2f[] raw = {v(25,2),v(29,6),v(31,12),v(31,25),v(40,25),v(40,29),v(32,31),v(29,41),v(28,46),v(24,49),v(21,45),v(19,35),v(17,30),v(10,29),v(10,25),v(18,24),v(20,11),v(22,5)};

	public Frigate(World world, boolean shieldOn) {
		super(raw, "pixmaps/foo.png", 50, 30, 1000, world, null);
		raiseShield = shieldOn;
		setWeaponType(new Laser(world, this));
	}

	public Frigate(World world) {
		super(raw, "pixmaps/foo.png", 50, 30, 1000, world, null);
		raiseShield = false;
		setWeaponType(new Laser(world, this));
	}

	public String getCause() {
		return "a frigate";
	}

	public String getTexturePath() {
		if (isVisible())
			return "pixmaps/foo.png";
		else
			return "pixmaps/foo-c.png";
	}

	protected Shield getShield() {
		return new OrangeShield(this, world);
	}

	public int getPointValue() {
		return 30;
	}

	protected float getMaxArmor() {
		return 1;
	}
}
