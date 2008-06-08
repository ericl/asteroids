package asteroids.weapons;
import static asteroids.Util.v;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.World;
import asteroids.bodies.Ship;
import asteroids.handlers.Stats;

/*
 * Just something thrown together quickly
 * basic missile sys capable of only guiding one missile right now
 */

public class MissileSys extends WeaponSys {

	public MissileSys(Ship s, World world, Weapon w) {
		super(s, world, w);
	}
	
	
	/*
	 * create the missile...("launch")
	 */
	public void fire() {
		fired.clear();
		Body target = getVictim();
		
		if (!canFire()) return;
		Weapon c = null;
		try {
			c = cons.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		c.setRotation(s.getRotation());
		float xc = (float)Math.sin(s.getRotation());
		float yc = (float)Math.cos(s.getRotation());
		float sr = s.getRadius() * 2 / 3; // estimated length
		c.setPosition(s.getPosition().getX()+xc*sr,s.getPosition().getY()-yc*sr);
		c.adjustVelocity(v(weapon.getSpeed()*xc,weapon.getSpeed()*-yc));
		c.adjustVelocity((Vector2f)s.getVelocity());
		c.addExcludedBody(s);
		fired.add(c);
		for (Stats stat : stats)
			stat.fired(c);
		world.add(c);
	}
	
	/*
	 * guide the missile...nah...it should just be a unguided rocket...nah...sajflkdsajg;lj
	 * hopefully the user won't be able to spam missiles...
	 */
	
	public void update() {
		Missile missile = (Missile)(fired.peek());
		Vector2f vec = new Vector2f(missile.getTarget().getVelocity());
		vec.add(missile.getVelocity());
		float xd = missile.getVelocity().getX() - missile.getTarget().getVelocity().getX();
		float yd = missile.getVelocity().getY() - missile.getTarget().getVelocity().getY();
		vec.scale(8e5f*missile.getMass());
		if(missile != null) {
			missile.adjustVelocity(new Vector2f(xd * 20, yd * 20));
			missile.addForce(vec);
		}
	}
	
	/*
	 * @return self-explantory
	 */
	protected Body getVictim() {
		BodyList potentialVictims = world.getBodies();
		float temp = s.getPosition().distance(potentialVictims.get(0).getPosition());
		Body target = potentialVictims.get(0);
		
		for(int count = 1; count < potentialVictims.size(); count++) {
			if(s.getPosition().distance(potentialVictims.get(count).getPosition()) < temp) {
				temp = s.getPosition().distance(potentialVictims.get(count).getPosition());
				target = potentialVictims.get(count);
			}
		}
		
		return target;
		
	}
}
