package asteroids.weapons;
import java.util.*;
import asteroids.bodies.*;
import asteroids.display.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	protected float lastFire = 0;
	protected boolean canFire = false;
	private boolean exploded;
	private long MAX_LIFETIME = 10000;
	private long startTime = System.currentTimeMillis();

	public Weapon(DynamicShape weap) {
		super(weap, 1);
	}

	public abstract float getSpeed();
	public abstract float getDamage();
	public abstract float getReloadTime();
	public abstract int getBurstLength();
	
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
		if (System.currentTimeMillis() - startTime > MAX_LIFETIME)
			return true;
		return exploded;
	}
}
