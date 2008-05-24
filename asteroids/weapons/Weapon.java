package asteroids.weapons;
import java.util.LinkedList;
import java.util.List;
import asteroids.bodies.*;
import asteroids.display.*;
import asteroids.handlers.Exploder;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

public abstract class Weapon extends Body implements Textured, Explodable {
	
	protected float reloadTime;		//reload time (ms)
	protected boolean canFire = false;
	private float lastFire;

	public Weapon(Polygon weap, float reload) {
		super(weap, weap.getArea());
		reloadTime = reload;
		lastFire = 0;
	}
	
	public Weapon(DynamicShape weap, int mass, float reload) {
		super(weap, mass);
		reloadTime = reload;
		lastFire = 0;
	}
	
	public void collided(CollisionEvent event) {
//		if (Exploder.worthyCollision(event))
//			explode = true;
	}
	
	public boolean canExplode() {
		return true;
	}
	
	// to be deleted
	public boolean canFire() {
		float current = System.currentTimeMillis();
		if (current - lastFire >= reloadTime * 100) {
			lastFire = current;
			return true;
		}
		return false;
	}
	
	public List<Body> explode() {
		List<Body> f = new LinkedList<Body>();
		return f;
	}
	
	public float getReloadTime() {
		return reloadTime;
	}
	
	public void fire() {
	}
	
	
}
