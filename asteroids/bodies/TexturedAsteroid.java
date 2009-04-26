/**
 * Polygonal asteroid that also implements Textured.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.List;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import asteroids.handlers.Exploder;

import static asteroids.Util.*;

public abstract class TexturedAsteroid extends TexturedPolyBody implements Asteroid, CauseOfDeath {
	protected float damage;
	protected Color color = Color.darkGray;

	public TexturedAsteroid(ROVector2f[] raw, String img, float nativesize, float size) {
		super(raw, img, nativesize, size);
	}

	public String getCause() {
		return "a textured asteroid";
	}

	public void addPowerups(List<Body> list) {
		for (int i = MIN_SIZE; i < Math.sqrt(getRadius()); i++)
			if (oneIn(BASE_CHANCE)) {
				list.add(PowerUp.random());
				return;
			}
	}

	public void setColor(Color c) {
		color = c;
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}

	public boolean canExplode() {
		if (getRadius() >= 100)
			return damage > getRadius() / 7; // these are not your normal asteroids!
		return damage > Math.log10(getRadius()) / 5;
	}

	public Color getColor() {
		return color;
	}
}
