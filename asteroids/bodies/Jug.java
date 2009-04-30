package asteroids.bodies;

import static asteroids.Util.*;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

public class Jug extends Entity {
	protected static ROVector2f[] raw = {v(33,8),v(43,8),v(51,14),v(54,22),v(54,30),v(50,38),v(46,44),v(52,50),v(55,56),v(61,53),v(66,53),v(70,48),v(74,52),v(74,57),v(62,63),v(56,64),v(54,72),v(22,72),v(20,66),v(14,64),v(2,57),v(2,51),v(5,48),v(11,54),v(15,54),v(21,55),v(25,47),v(30,44),v(22,34),v(20,26),v(23,16)};

	public Jug(World world) {
		super(raw, "pixmaps/jug.png", 80, 48, 5500, world, new Cannon());
		this.defaultShield = raiseShield = true;
		setMaxVelocity(20,20);
	}

	public String getCause() {
		return "a juggernaut";
	}

	public void reset() {
		super.reset();
		setWeaponType(new Cannon());
	}

	protected Shield getShield() {
		Shield s = new OrangeShield(this, world, 11);
		s.removeBit(BIT_SHIELD_PENETRATING);
		return s;
	}

	public Body getRemnant() {
		deaths++;
		updateShield();
		return explosion = new LargeExplosion(Explosion.TrackingMode.NONE, 2.0f);
	}

	public int getPointValue() {
		return 300;
	}

	protected float getMaxArmor() {
		return 4;
	}
}
