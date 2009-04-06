/**
 * An abstract polygonal asteroid that calculates scaling, center of mass,
 * and geometry automatically.
 */

package asteroids.bodies;

import java.util.List;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import asteroids.handlers.Exploder;

import static asteroids.Util.*;

public abstract class PolyAsteroid extends PolyBody implements Asteroid {
	protected float damage;

	public PolyAsteroid(ROVector2f[] raw, float scale) {
		super(raw, scale);
	}

	public PolyAsteroid(ROVector2f[] raw, float scale, float mass) {
		super(raw, scale, mass);
	}

	public void addPowerups(List<Body> list) {
		for (int i = MIN_SIZE; i < Math.sqrt(getRadius()); i++)
			if (oneIn(BASE_CHANCE)) {
				list.add(PowerUp.random());
				return;
			}
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}

	public boolean canExplode() {
		if (getRadius() >= 100)
			return damage > getRadius() / 7; // these are not your normal asteroids!
		return damage > Math.log10(getRadius()) / 5;
	}
}
