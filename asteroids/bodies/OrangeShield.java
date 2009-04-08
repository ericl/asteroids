package asteroids.bodies;

import asteroids.ai.*;

public class OrangeShield extends Shield {
	public OrangeShield(Entity ship) {
		super(ship);
	}

	public String getTexturePath() {
		return "pixmaps/orange-shield.png";
	}

	public float getMax() {
		return 2;
	}
}
