package asteroids.weapons;
import static asteroids.Util.v;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.World;
import asteroids.bodies.Asteroid;
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
		Body target = getVictim();
		
		if (!canFire()) return;
		Weapon c = null;
		try {
			c = cons.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Missile mis = (Missile)c;
		mis.setRotation(s.getRotation());
		float xc = (float)Math.sin(s.getRotation());
		float yc = (float)Math.cos(s.getRotation());
		float sr = s.getRadius() * 2 / 3; // estimated length
		mis.setPosition(s.getPosition().getX()+xc*sr,s.getPosition().getY()-yc*sr);
		mis.adjustVelocity(v(weapon.getSpeed()*xc,weapon.getSpeed()*-yc));
		mis.adjustVelocity((Vector2f)s.getVelocity());
		mis.addExcludedBody(s);
		mis.setTarget(target);
		for (Stats stat : stats)
			stat.fired(mis);
		world.add(mis);
	}
	
	
	/*
	 * to be deleted
	 * missiles guide themselves with their endFrame()
	 */
	
	public void update() {
//		Missile missile = (Missile)(fired.peek());
//		Vector2f vec = new Vector2f(missile.getTarget().getVelocity());
//		vec.add(missile.getVelocity());
//		float xd = missile.getVelocity().getX() - missile.getTarget().getVelocity().getX();
//		float yd = missile.getVelocity().getY() - missile.getTarget().getVelocity().getY();
//		vec.scale(8e5f*missile.getMass());
//		if(missile != null) {
//			missile.adjustVelocity(new Vector2f(xd * 20, yd * 20));
//			missile.addForce(vec);
//		}
	}
	
	/*
	 * @return self-explantory
	 */
	protected Body getVictim() {
		// lots of target: no idea...
		
		// get list of bodies in world
		BodyList bodies = world.getBodies();
		BodyList potentialVictims = new BodyList();
		
		//quick and dirty...
		for(int i = 0; i < bodies.size(); i++) {
			if(!(bodies.get(i) instanceof Ship)) {
				potentialVictims.add(bodies.get(i));
			}
		}
		
		// get the distance from one of the bodies
		float temp = s.getPosition().distance(potentialVictims.get(0).getPosition());
		Body target = potentialVictims.get(0);
		
		// compare the distance with others
		// if one lower, make that the target
		for(int count = 1; count < potentialVictims.size(); count++) {
			if(s.getPosition().distance(potentialVictims.get(count).getPosition()) < temp) {
				temp = s.getPosition().distance(potentialVictims.get(count).getPosition());
				target = potentialVictims.get(count);
			}
		}
		
		System.out.println(temp);
		String tar;
		if(target instanceof Ship) tar = "ship";
		if(target instanceof Asteroid) tar = "asteroid";
		else tar = "no idea...>.<";
		System.out.println("Target: " + tar);
		
		return target;
		
	}
}
