package asteroids.weapons;
import java.util.*;
import asteroids.bodies.*;
import asteroids.display.*;
import asteroids.handlers.Timer;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	private boolean exploded;
	private long MAX_LIFETIME = 10000;
	private long startTime = Timer.gameTime();
	protected float lastFire = 0;
	protected boolean canFire = false;
	protected int level = 0;
	public final static int MAX_LEVEL = 2;

	public Weapon(DynamicShape weap, float mass) {
		super(weap, mass);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		if (level > MAX_LEVEL || level < 0)
			throw new IllegalArgumentException();
		level = l;
	}

	public void incrementLevel() {
		if (level < 3)
			level++;
	}

	public abstract float getSpeed();
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

	public boolean exploded() {
		if (Timer.gameTime() - startTime > MAX_LIFETIME)
			return true;
		return exploded;
	}
}
