/**
 * Weapons are emitted from the ship by a weapon system.
 * Weapons are handled as a special case by Exploder.
 */

package asteroids.weapons;

import java.awt.Color;

import java.util.*;

import asteroids.bodies.*;

import asteroids.display.*;

import asteroids.handlers.Timer;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	private boolean exploded;
	private long startTime = Timer.gameTime();
	protected float lastFire = 0;
	protected boolean canFire = false;
	protected int level = 0;
	protected Body origin;

	public Weapon(DynamicShape weap, float mass) {
		super(weap, mass);
	}

	public Color getColor() {
		return Color.ORANGE;
	}

	public int getLevel() {
		return level;
	}

	public Body getOrigin() {
		return origin;
	}

	/**
	 * precondition: origin implements Visible
	 */
	public void setOrigin(Body origin) {
		if (!(origin instanceof Visible))
			throw new IllegalArgumentException("body not instanceof Visible");
		this.origin = origin;
	}

	public void setLevel(int l) {
		level = l;
	}

	public void incrementLevel() {
		level++;
	}

	public abstract float getLaunchSpeed();
	public abstract float getWeaponSpeed();
	public abstract float getDamage();
	public abstract float getReloadTime();

	public int getBurstLength() {
		return 0;
	}

	public int getNum() {
		return 1;
	}
	
	public void collided(CollisionEvent event) {}
	
	public boolean canExplode() {
		return true;
	}
	
	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		exploded = true;
		return f;
	}

	protected long getLifetime() {
		return 10000;
	}

	public boolean exploded() {
		if (Timer.gameTime() - startTime > getLifetime())
			return true;
		return exploded;
	}
}
