package asteroids.bodies;

import net.phys2d.raw.*;

import asteroids.weapons.*;

import asteroids.display.*;

public class OrangeShield extends Shield {

	public OrangeShield(Visible ship, World world) {
		super(ship, world);
	}

	public OrangeShield(Visible ship, World world, float max) {
		super(ship, world);
		this.MAX = max;
	}

	public String getTexturePath() {
		// orange shields do not have the "cloak" effect because
		// the player is not using it... (looks weird with ai)
		return damage > MAX ? "" : "pixmaps/orange-shield.png";
	}

	public Body getRemnant() {
		return new OrangeShieldFailing(source, radius);
	}

	public float getMax() {
		return MAX;
	}
}
