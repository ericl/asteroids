/**
 * 5 extra missiles.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class BeamPower extends PowerUp {
	private static ROVector2f[] raw = { v(111,23), v(145,78), v(205,90), v(162,137), v(171,199), v(111,174), v(54,201), v(62,139), v(19,92), v(79,78)};

	public BeamPower() {
		super(raw, "pixmaps/beams.png", 112.5f, 9f);
		setRotDamping(1);
	}

	protected void up(Enhancable ship) {
		ship.gainBeams(1000);
	}
}
