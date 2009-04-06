/**
 * User-controlled ship in the world.
 */

package asteroids.bodies;

import asteroids.ai.*;

import net.phys2d.raw.*;

public class ComputerShip extends Ship {
	private AI ai;
	private boolean delay;

	public ComputerShip(World w) {
		super(w);
		ai = new ShipAI(w, this);
	}

	public ComputerShip(World w, boolean delay) {
		super(w);
		ai = new ShipAI(w, this);
		this.delay = delay;
		if (delay)
			activeTime = ACTIVE_DEFAULT;
	}

	public void reset() {
		super.reset();
		activeTime = delay ? ACTIVE_DEFAULT : 0;
	}

	protected void torque() {
		// setTorque() is unpredictable with varied dt
		adjustAngularVelocity(getMass()*torque);
	}

	public void endFrame() {
		super.endFrame();
		if (activeTime-- < 1)
			ai.update();
	}
}
