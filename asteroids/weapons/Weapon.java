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

import net.phys2d.math.*;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

public abstract class Weapon extends PObj implements Textured, Explodable {
	private boolean exploded;
	protected final World world;
	private long startTime = Timer.gameTime();
	protected float lastFire = 0;
	protected boolean canFire = false;
	protected int level = 0;
	protected final Body origin;

	public Weapon(DynamicShape weap, float mass, World world, Body origin) {
		super(weap, mass);
		this.world = world;
		this.origin = origin;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public boolean isMaxed() {
		return false;
	}

	public boolean hasPreferredRotation() {
		return false;
	}

	// precondition: hasPreferredRotation has returned true
	public float getPreferredRotation() {
		return 0;
	}

	public boolean hasPreferredVelocity() {
		return false;
	}

	public void hintVelocity(Vector2f vel) {
		adjustVelocity(vel);
	}

	// precondition: hasPreferredVelocity has returned true
	public Vector2f getPreferredVelocity() {
		return null;
	}

	public abstract Weapon duplicate();

	public boolean preferDrawableFallback() {
		return false;
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

	public void endFrame() {
		super.endFrame();
		if (exploded())
			world.remove(this);
	}

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
