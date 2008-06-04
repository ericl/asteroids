package asteroids.weapons;
import java.util.*;
import asteroids.bodies.*;
import asteroids.display.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	protected float lastFire = 0;
	protected boolean canFire = false;
	protected long activeTime, deactivateTime = 5;

	public Weapon(DynamicShape weap) {
		super(weap, 1);
		activeTime = System.currentTimeMillis();
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
		return f;
	}
	
	public boolean check() {
		long temp = System.currentTimeMillis();
		if (temp - activeTime >= deactivateTime * 10) return true;
		activeTime = temp;
		return false;
	}
}
