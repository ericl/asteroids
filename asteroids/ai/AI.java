package asteroids.ai;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

public abstract class AI {
	protected Automated ship;
	protected World world;
	protected int steps;
	protected ROVector2f targetPos;
	protected Targetable target;

	public AI(World world, Automated ship) {
		this.world = world;
		this.ship = ship;
	}

	protected boolean canTarget(Targetable object) {
		return object.canTarget();
	}

	protected void selectTarget() {
		float min_dist = -1;
		target = null;
		BodyList bodies = world.getBodies();
		float d = 0;
		for (int i=0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b == ship || !(b instanceof Targetable) || !canTarget((Targetable)b))
				continue;
			Targetable t = (Targetable)b;
			if (target == null)
				target = t;
			d = Math.abs(ship.getPosition().distance(t.getPosition()));
			if (min_dist < 0)
				min_dist = d;
			else if (d < min_dist) {
				target = t;
				min_dist = d;
			}
		}
	}

	protected abstract float getMaxTorque();
	protected abstract float minTorqueThreshold();

	public static ROVector2f predictTargetPosition(Automated origin, Targetable target, float speed, boolean movingOrigin) {
		float timeElapsed = sub(origin.getPosition(), target.getPosition()).length() / speed;
		ROVector2f pos = target.getPosition();
		Vector2f v = scale(target.getVelocity(), timeElapsed);
		Vector2f o = movingOrigin ? scale(origin.getVelocity(), timeElapsed) : v(0,0);
		return v(pos.getX() + v.getX() - o.getX(), pos.getY() + v.getY() - o.getY());
	}

	protected boolean trackTarget() {
		if (target == null || !target.canTarget())
			return false;
		targetPos = predictTargetPosition(ship, target, ship.getWeaponSpeed(), true);
		Vector2f ds = sub(ship.getPosition(), targetPos);
		double tFinal = Math.atan2(ds.getY(), ds.getX()) - Math.PI/2;
		double tInit1 = (ship.getRotation() % (2*Math.PI));
		double tInit2 = tInit1 - sign((float)tInit1)*2*Math.PI;
		double delta1 = tFinal - tInit1;
		double delta2 = tFinal - tInit2;
		double delta = Math.abs(delta1) > Math.abs(delta2) ? delta2 : delta1;
		float torque = (float)(delta * getMaxTorque());
		float x = minTorqueThreshold();
		if (torque > 0 && torque < x) {
			ship.modifyTorque(x);
			return true;
		} else if (torque < 0 && torque > -x) {
			ship.modifyTorque(-x);
			return true;
		}
		ship.modifyTorque(torque);
		return false;
	}

	public void update() {
		if (steps % 300 == 0)
			selectTarget();
		if (steps % 5 == 0 && trackTarget()) {
			ship.fire();
			if (ship.health() < .2)
				ship.launchMissile();
		}
		steps++;
	}
}
