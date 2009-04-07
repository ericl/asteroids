package asteroids.ai;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

import net.phys2d.raw.World;

public class ShipAI extends AI {

	public ShipAI(World world, Automated ship) {
		super(world, ship);
	}

	public float getMaxTorque() {
		return 8e-5f;
	}

	public float minTorqueThreshold() {
		return 1e-5f;
	}

	public void adjustAccel() {
		float accel = 0;
		if (targetPos == null) {
			accel = range(-2,3);
		} else {
			float distance = sub(targetPos, ship.getPosition()).length();
			if (distance < 300 && ship.health() < .2)
				accel = range(-4,-2);
			else if (distance > 350)
				accel = range(2,5);
			else if (distance > 150)
				accel = range(0,2);
			else
				accel = range(-3,-1);
		}
		ship.setAccel(accel);
	}

	public void update() {
		super.update();
		if (steps % 100 == 66)
			adjustAccel();
	}
}
