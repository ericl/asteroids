package asteroids.bodies;

import net.phys2d.raw.*;
import net.phys2d.math.*;

import asteroids.weapons.*;

import static asteroids.Util.*;

public class KShip extends Entity {
	protected static ROVector2f[] raw = {v(14,0),v(24,1),v(23,12),v(25,24),v(31,29),v(31,16),v(35,16),v(35,31),v(39,37),v(38,41),v(29,44),v(11,44),v(3,41),v(1,38),v(1,34),v(4,32),v(4,16),v(8,16),v(9,31),v(14,26),v(17,16),v(17,9),v(14,6)};

	public KShip(World world) {
		super(raw, "pixmaps/kship.png", 40, 25, 1400, world, new Laser2(1));
	}

	public int getPointValue() {
		return 110;
	}

	protected float getMaxArmor() {
		return 5;
	}
}
