package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.World;
import asteroids.bodies.Ship;
import asteroids.handlers.Stats;
import static asteroids.Util.*;

public class WeaponsSys {
	private Weapon weapon;
	private long lastFired = 0;
	private Queue<Weapon> fired = new LinkedList<Weapon>();
	private Stats stats;

	public WeaponsSys(Weapon w, Stats s) {
		weapon = w;
		stats = s;
	}

	public boolean canFire() {
		long current = System.currentTimeMillis();
		if ((current-lastFired)>=(long)(weapon.getReloadTime()*100)) {
			lastFired = current;
			return true;
		}
		return false;
	}
	
	public void fire(Ship s, World w) {
		if (canFire()) {
			stats.att++;
			Laser c = new Laser();
			c.setRotation(s.getRotation());
			float ax = (float)(20*Math.sin(s.getRotation()));
			float ay = (float)(20*Math.cos(s.getRotation()));
			c.setPosition(s.getPosition().getX()+ax, s.getPosition().getY()-ay);
			c.adjustVelocity(v(20*ax,20*-ay));
			c.addExcludedBody(s);
			fired.add(c);
			w.add(c);
		}
	}
	
	public void tracker(World w) {
		if (!fired.isEmpty() && fired.peek().check())
			w.remove(fired.remove());
	}
}
