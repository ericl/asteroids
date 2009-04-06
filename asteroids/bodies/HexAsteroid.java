/**
 * Blue hexagon-like asteroid that shatters nicely.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public class HexAsteroid extends PolyAsteroid {
	private static ROVector2f[] geo = {v(-30,0),v(-10,-10),v(10,-10),v(30,0),v(10,20),v(-10,20)};

	public HexAsteroid(float size) {
		super(geo, size / 25);
		setColor(Color.BLUE);
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new HexAsteroid(getRadius() / 2) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		HexAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new HexAsteroid(getRadius() / 3);
				tmp.setColor(getColor());
				f.add(tmp);
			}
		addPowerups(f);
		return f;
	}
}
