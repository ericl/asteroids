package asteroids.bodies;

import static asteroids.Util.v;

import java.util.ArrayList;
import java.util.List;

import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;

public class MediumAsteroid extends TexturedAsteroid {

	private static ROVector2f[] raw = {v(57,2), v(96,17), v(103,27), v(103,41), v(108,55), v(100,79), v(89,87), v(87,94), v(54,102), 
		v(15,85), v(17,82), v(0,57), v(3,51), v(1,44), v(5,39), v(6,29)};
	
	public MediumAsteroid(float size) {
		super(raw, "pixmaps/2.png", 50, size);
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new SmallAsteroid(getRadius() / 2) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		SmallAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new SmallAsteroid(getRadius() / 3);
				f.add(tmp);
			}
		return f;
	}
}
