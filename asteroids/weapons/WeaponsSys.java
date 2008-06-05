package asteroids.weapons;
import java.util.*;
import java.lang.reflect.*;
import net.phys2d.raw.World;
import net.phys2d.math.*;
import asteroids.bodies.Ship;
import asteroids.handlers.Stats;
import static asteroids.Util.*;

public class WeaponsSys {
	
	protected Weapon weapon;
	protected Constructor<Weapon> cons;
	protected long lastFired = 0;
	protected Queue<Weapon> fired = new LinkedList<Weapon>();
	protected Stats stats;
	protected float burst;

	public WeaponsSys(Weapon w, Stats s) {
		stats = s;
		setWeaponType(w);
	}

	@SuppressWarnings(value = "unchecked")
	public void setWeaponType(Weapon w) {
		weapon = w;
		try {
			cons = (Constructor<Weapon>)weapon.getClass().getConstructor();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public boolean canFire() {
		long current = System.currentTimeMillis();
		long disp = current - lastFired;
		float test = Math.min(disp / weapon.getReloadTime(), weapon.getBurstLength());
		if (test > burst)
			burst = test;
		if (burst > 1 && disp > weapon.getReloadTime() / 4
				|| disp > weapon.getReloadTime()) {
			burst--;
			lastFired = current;
			return true;
		}
		return false;
	}
	
	public void fire(Ship s, World w) {
		if (!canFire()) return;
		Weapon c = null;
		try {
			c = cons.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stats.att++;
		c.setRotation(s.getRotation());
		float xc = (float)Math.sin(s.getRotation());
		float yc = (float)Math.cos(s.getRotation());
		float sr = s.getRadius() * 2 / 3; // estimated length
		c.setPosition(s.getPosition().getX()+xc*sr,s.getPosition().getY()-yc*sr);
		c.adjustVelocity(v(weapon.getSpeed()*xc,weapon.getSpeed()*-yc));
		c.adjustVelocity((Vector2f)s.getVelocity());
		c.addExcludedBody(s);
		for (Weapon f : fired)
			c.addExcludedBody(f);
		fired.add(c);
		w.add(c);
	}
	
	public void update(World w) {
		while (!fired.isEmpty() && fired.peek().exploded())
			w.remove(fired.remove());
	}
}
