package asteroids.bodies;

import java.awt.Color;

public class SpaceDebris extends HexAsteroid {
	public SpaceDebris(float size) {
		super(size);
		setColor(Color.GRAY);
	}

	public String getCause() {
		return "space junk";
	}
}
