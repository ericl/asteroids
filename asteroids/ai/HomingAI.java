package asteroids.ai;

import java.util.HashSet;
import java.util.Set;

import net.phys2d.raw.World;

public class HomingAI extends AI {
	private Set<Object> whitelist = new HashSet<Object>();

	public HomingAI(World world, Automated ship) {
		super(world, ship);
	}

	public float getMaxTorque() {
		return 3e-5f;
	}

	public void addExcluded(Object o) {
		whitelist.add(o);
	}

	public float minTorqueThreshold() {
		return 3e-5f;
	}

	public boolean canTarget(Targetable t) {
		return super.canTarget(t) && !whitelist.contains(t);
	}

	public void update() {
		if (steps % 100 == 0)
			selectTarget();
		trackTarget();
		steps++;
	}
}
