package asteroids.bodies;

import java.awt.Color;

import java.util.List;

import asteroids.weapons.Laser3;
import asteroids.weapons.ShieldFailing;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public class Terror extends Entity {
	protected static ROVector2f[] raw = {v(21,11),v(43,11),v(56,26),v(58,46),v(32,58),v(5,46),v(5,26)};
	
	// allows them to swarm a bit
	protected boolean rogue = oneIn(3);

	public Terror(World world) {
		super(raw, null, 64, 64, 7500, world, new Laser3());
		setColor(Color.BLUE);
	}

	public String getCause() {
		return "a blue terror";
	}

	protected float getMaxArmor() {
		return 10;
	}

	public Body getRemnant() {
		return new ShieldFailing(this, getRadius());
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

	public boolean targetableBy(Object o) {
		return rogue || !(o instanceof Terror);
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
}
