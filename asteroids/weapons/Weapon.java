package asteroids.weapons;
import java.util.*;
import asteroids.bodies.*;
import asteroids.display.*;
import asteroids.handlers.Exploder;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	protected float reloadTime, lastFire;
	protected boolean canFire = false;
	protected long activeTime, deactivateTime;

	public Weapon(Polygon weap, float reload) {
		super(weap, weap.getArea());
		reloadTime = reload;
		activeTime = System.currentTimeMillis();
		deactivateTime = 5;
		lastFire = 0;
	}
	
	public Weapon(DynamicShape weap, int mass, float reload) {
		super(weap, mass);
		reloadTime = reload;
		activeTime = System.currentTimeMillis();
		deactivateTime = 5;
		lastFire = 0;
	}
	
	public void collided(CollisionEvent event) {}
	
	public boolean canExplode() {
		return true;
	}
	
	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		return f;
	}
	
	public float getReloadTime() {
		return reloadTime;
	}
	
	public boolean check() {
		long temp = System.currentTimeMillis();
		if(temp - activeTime >= deactivateTime * 10) return true;
		else
			activeTime = temp;
		return false;
	}
}
