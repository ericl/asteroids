package asteroids.weapons;
import java.util.List;

import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;


public class Missile extends Weapon {
	protected Body myTarget;
	
	public Missile() {
		super(new Circle(10));
	}
	
	public Body getRemnant() {
		return new LaserExplosion();
	}
	
	public List<Body> getFragments() {
		return null;
	}
	
	public void setTarget(Body b) {
		myTarget = b;
	}
	
	public Body getTarget() {
		return myTarget;
	}
	
	public int getBurstLength() {
		return 0;
	}
	public float getDamage() {
		return .1f;
	}
	
	public void endFrame() {
		
	}
	
	public float getReloadTime() {
		return 300;
	}
	
	public float getSpeed() {
		return 75;
	}
	
	public String getTexturePath() {
		return "pixmaps/laser.png";
	}

	public float getTextureScaleFactor() {
		return 2;
	}

	public Vector2f getTextureCenter() {
		return v(0,0);
	}

	public float getRadius() {
		return 0;
	}
}
