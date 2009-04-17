package asteroids.bodies;

import java.awt.Color;

import asteroids.weapons.Laser3;

import java.util.List;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public class Terror extends Entity {
	protected static ROVector2f[] raw = {v(21,11),v(43,11),v(56,26),v(58,46),v(32,58),v(5,46),v(5,26)};
	protected int thrust;
	
	// terrors tend to team up on others
	protected boolean rogue = oneIn(2);

	public Terror(World world) {
		super(raw, null, 64, 64, 7500, world, new Laser3());
		setColor(Color.BLUE);
	}

	protected float getMaxArmor() {
		return 10;
	}

	public Body getRemnant() {
		return null;
	}

	public int getPointValue() {
		return 200;
	}

	public void setAccel(float accel) {
		this.accel = accel / 3;
	}

	public void modifyTorque(float t) {
		torque = t / 2;
	}

	public boolean canTarget() {
		return rogue;
	}

	public List<Body> getFragments() {
		List<Body> f = super.getFragments();
		for (Body b : f)
			if (b instanceof PolyBody)
				((PolyBody)b).setColor(Color.BLUE);
		return f;
	}

	public boolean preferDrawableFallback() {
		return true;
	}

	public void reset() {
		super.reset();
		weapons.setWeaponType(new Laser3());
	}

	public void endFrame() {
		super.endFrame();
		thrust--;
	}

	protected void accel() {
		if (accel > 0)
			thrust = 5;
		super.accel();
	}
}
