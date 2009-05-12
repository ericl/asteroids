/**
 * Don't ask - part of calculating mass w/inheritance...
 */

package asteroids.bodies;

import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;

public abstract class PolyBodyProxy extends PObj {
	public PolyBodyProxy(Polygon poly) {
		super(poly, poly.getArea());
		setRestitution(.5f);
	}

	public PolyBodyProxy(Polygon poly, float mass) {
		super(poly, mass);
		setRestitution(.5f);
	}
}
