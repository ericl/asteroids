package asteroids.ai;

import net.phys2d.math.ROVector2f;

public interface Automated extends Targetable {
	public void setAI(AI ai);
	public float getRotation();
	public ROVector2f getPosition();
	public ROVector2f getVelocity();
	public void setDamping(float damping);
	public void modifyTorque(float torque);
	public void setAccel(float accel);
	public float getWeaponSpeed();
	public void cloak();
	public void uncloak();
	public void selfDestruct();
	public void startFiring();
	public void startLaunching();
	public void stopFiring();
	public void stopLaunching();

	/**
	 * @return true if the automated successfully fired
	 */
	public boolean fire(float rotation);

	/**
	 * @return true if the automated successfully launched a missile
	 */
	public boolean launchMissile();

	/**
	 * @return health of ship as fraction of 1
	 */
	public double health();
}
