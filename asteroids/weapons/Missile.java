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
	
	public void endFrame() {
		if(getTarget() != null) {
			System.out.println("target locked");
			Vector2f vec = new Vector2f(getTarget().getVelocity());
			vec.add(getVelocity());
			float xd = getVelocity().getX() - getTarget().getVelocity().getX();
			float yd = getVelocity().getY() - getTarget().getVelocity().getY();
			vec.scale(2);
			adjustVelocity(new Vector2f(xd * 20, yd * 20));
			addForce(vec);
		}
	}
	
}
