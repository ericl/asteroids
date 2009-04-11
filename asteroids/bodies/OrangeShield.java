package asteroids.bodies;

import asteroids.display.Visible;

public class OrangeShield extends Shield {
	public OrangeShield(Visible ship) {
		super(ship);
	}

	public String getTexturePath() {
		return damage > getMax() ? "" : "pixmaps/orange-shield.png";
	}

	public float getMax() {
		return 2f;
	}
}
