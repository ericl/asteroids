package asteroids.ai;

import net.phys2d.math.ROVector2f;

public interface Automated {
	public float getRotation();
	public ROVector2f getPosition();
	public ROVector2f getVelocity();
	public void setDamping(float damping);
	public void modifyTorque(float torque);
	public void setAccel(float accel);
	public float getWeaponSpeed();

	/**
	 * @return true if the automated successfully fired
	 */
	public boolean fire();

	/**
	 * @return true if the automated successfully launched a missile
	 */
	public boolean launchMissile();

	/**
	 * @return health of ship as fraction of 1
	 */
	public double health();
}
