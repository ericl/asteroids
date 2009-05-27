/**
 * Handles mechanics of weapons fire from ships.
 */

package asteroids.weapons;

import asteroids.display.*;

import asteroids.handlers.Timer;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public class WeaponSys {
	protected static float ANGULAR_DISTRIBUTION = (float)Math.PI/48;
	protected Body origin;
	protected World world;
	protected Weapon weapon;
	protected long lastFired;
	protected float burst;

	public WeaponSys(Body origin, World wo, Weapon w) {
		if (w == null)
			setWeaponType(new Laser(wo, origin));
		else
			setWeaponType(w);
		this.origin = origin;
		world = wo;
	}

	public void upgrade() {
		weapon.incrementLevel();
	}

	public boolean isMaxed() {
		return weapon.isMaxed();
	}

	public void setWeaponType(Weapon w) {
		weapon = w;
		lastFired = 0;
		burst = 0;
	}

	private boolean canFire() {
		long current = Timer.gameTime();
		long disp = current - lastFired;
		float test = Math.min(disp / weapon.getReloadTime(), weapon.getBurstLength());
		if (test > burst)
			burst = test;
		if (burst > 1 && disp > weapon.getReloadTime() / 4
				|| disp > weapon.getReloadTime()) {
			burst--;
			lastFired = current;
			return true;
		}
		return false;
	}

	public boolean fire() {
		return fire(origin.getRotation());
	}

	public boolean fire(float rotation) {
		if (!canFire())
			return false;
		float initialAngle = (weapon.getNum()-1)*ANGULAR_DISTRIBUTION/2;
		for (int i=0; i < weapon.getNum(); i++) {
			Weapon weap = makeWeapon(i*ANGULAR_DISTRIBUTION - initialAngle, rotation);
			world.add(weap);
		}
		return true;
	}

	// postcondition: nothing is modified
	private Weapon makeWeapon(float angle, float originRotation) {
		final Weapon c = weapon.duplicate();
		if (weapon.hasPreferredRotation())
			originRotation = weapon.getPreferredRotation();
		c.setRotation(originRotation+angle);
		float xc = (float)Math.sin(originRotation+angle);
		float yc = (float)Math.cos(originRotation+angle);
		float sr = ((Visible)origin).getRadius(); // estimated length
		c.setPosition(origin.getPosition().getX()+xc*sr, origin.getPosition().getY()-yc*sr);
		Vector2f vel = v(weapon.getLaunchSpeed()*xc, weapon.getLaunchSpeed()*-yc);
		if (weapon.hasPreferredVelocity())
			c.adjustVelocity(weapon.getPreferredVelocity());
		else
			c.hintVelocity(vel);
		c.adjustVelocity((Vector2f)origin.getVelocity());
		c.setGroup(origin.getGroup());
		return c;
	}

	public float getWeaponSpeed() {
		return weapon.getWeaponSpeed();
	}

	public boolean isBeam() {
		return weapon instanceof Beam;
	}
}
