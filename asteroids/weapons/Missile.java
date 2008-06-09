package asteroids.weapons;
import java.util.List;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;


public class Missile extends Weapon {
	protected Body myTarget;
	
	// to be deleted by will
	private static float myRadius = 2;
	/////
	
	public Missile() {
		super(new Circle(myRadius), 1);
	}
	
	public Vector2f getTextureCenter() {
		return v(7,3);
	}
	
	public boolean canExplode() {
		return true;
	}
	
	public float getDamage() {
		return .1f;
	}
	
	public float getReloadTime() {
		return 300;
	}
	
	public float getSpeed() {
		return 75;
	}
	
	public Body getRemnant() {
		return new LaserExplosion();
	}
	
	public List<Body> getFragments() {
		return null;
	}
	
	public String getTexturePath() {
		return "pixmaps/laser.png";
	}

	public float getTextureScaleFactor() {
		return 2;
	}	
	
	public float getRadius() {
		return 2;
	}
	
	public void setTarget(Body b) {
		myTarget = b;
	}
	
	public Body getTarget() {
		return myTarget;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see net.phys2d.raw.Body#endFrame()
	 * blah blah blah... ^ eclipse
	 * 
	 * problem exists below...
	 * when commented/removed, missiles display on screen fine and act like lasers
	 * when included, missiles magicly disappear
	 */
	public void endFrame() {
		if(getTarget() != null) {
//			System.out.println("target locked");
			
			// get the target's velocity
			Vector2f vec = new Vector2f(getTarget().getVelocity());
			
			// now add it to the missiles velocity (vectors)
			vec.add(getVelocity());
			
			// get the difference in the missiles velocity and target velocity
			float xd = getVelocity().getX() - getTarget().getVelocity().getX();
			float yd = getVelocity().getY() - getTarget().getVelocity().getY();
			

			
			// adjust the velocity of the missile
			adjustVelocity(new Vector2f(xd * 0.02f, yd * 0.02f));
			
			// scale
			vec.scale(2);
			
			// add a force in same direction(after all, missiles do acceleration);
			addForce(vec);
		}
	}
	
}
