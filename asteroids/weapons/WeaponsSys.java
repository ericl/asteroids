package asteroids.weapons;

import java.util.*;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.World;
import asteroids.bodies.Ship;

public class WeaponsSys {

	private Weapon weapon;
	private long lastFired;
	private int blah = 0;
	
	public WeaponsSys(Weapon w) {
		weapon = w;
		lastFired = 0;
	}
	
	public boolean canFire() {
		long current = System.currentTimeMillis();

		if((current - lastFired) >= (long)(weapon.getReloadTime() * 100)) {
			System.out.println(current - lastFired);
			lastFired = current;
			return true;
		}
		return false;
	}
	
	public void fire(Ship s, World w) {
		blah++;
//		System.out.println(blah + " FIRE ATTEMPT");
		if(canFire()) {
			System.out.println("FIRE SUCCESS");
			Laser c = new Laser();
			c.setRotation(s.getRotation());
			float ax = (float)(20*Math.sin(s.getRotation()));
			float ay = (float)(20*Math.cos(s.getRotation()));
			c.setPosition(s.getPosition().getX()+ax, s.getPosition().getY()-ay);
			c.adjustVelocity(v(20*ax,20*-ay));
			c.addExcludedBody(s);
			w.add(c);
		} else {
//			System.out.println("FIRE SYS FAILED");
//			System.out.println("CURRENT: " + System.currentTimeMillis() + "\tLAST FIRED: " + lastFired);
		}
	}
	
	private static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}
	
//	public WeaponsSys(List<Weapon> weapon) {
//		for(Weapon w : weapon) {
//			weapons.add(w);
//		}
//	}
	
	
}
