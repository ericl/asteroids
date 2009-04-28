/**
 * Blue hexagon-like asteroid that shatters nicely.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public class HexAsteroid extends PolyAsteroid implements CauseOfDeath {
	private static ROVector2f[] geo = {v(-30,0),v(-10,-10),v(10,-10),v(30,0),v(10,20),v(-10,20)};

	public HexAsteroid(float size) {
		super(geo, size / 25);
		setColor(Color.BLUE);
	}

	public String getCause() {
		return getRadius() > 99 ? "a massive hexagon" : "a hexagonal asteroid";
	}

	public HexAsteroid(float size, Color c) {
		super(geo, size / 25);
		setColor(c);
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new HexAsteroid(getRadius() / 2, getColor()) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(7);
		if (getRadius() > 10)
			for (int i=0; i < 6; i++)
				f.add(new HexAsteroid(getRadius() / 3, getColor()));
		addPowerups(f);
		return f;
	}
}
