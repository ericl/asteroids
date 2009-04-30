package asteroids.weapons;

import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;

import asteroids.ai.*;
import asteroids.bodies.*;
import static asteroids.Util.*;

public class Missile extends Weapon implements Automated, Heavy {
	private static float myRadius = 2;
	private HomingAI ai;
	private float torque;
	private World world;
	private boolean explode;

	public Missile(World w) {
		super(new Circle(myRadius), 1000);
		world = w;
		ai = new HomingAI(world, this);
		setMaxVelocity(40, 40);
		setRotDamping(100);
	}

	public Missile duplicate() {
		return new Missile(world);
	}

	public void setAI(AI ai) {}
	public void startFiring() {}
	public void stopFiring() {}
	public void startLaunching() {}
	public void stopLaunching() {}
	public void selfDestruct() {}
	public void cloak() {}
	public void uncloak() {}
	public void setAccel(float a) {}

	public boolean fire(float rotation) {
		return false;
	}

	public void modifyTorque(float t) {
		torque = t;
	}

	public float getWeaponSpeed() {
		return 55;
	}

	public boolean isVisible() {
		return false;
	}

	public boolean targetableBy(Object o) {
		return false;
	}

	public int getPointValue() {
		return 0;
	}

	public boolean launchMissile() {
		return false;
	}

	public boolean fire() {
		return false;
	}

	public double health() {
		return 1;
	}

	public void addExcludedBody(Body b) {
		ai.addExcluded(b);
		super.addExcludedBody(b);
	}

	public void endFrame() {
		super.endFrame();
		Vector2f dir = direction(getRotation());
		addForce(v(20*getMass()*dir.getX(),20*getMass()*dir.getY()));
		adjustAngularVelocity(getMass()*torque);
		ai.update();
	}

	public Body getRemnant() {
		return new LargeExplosion(Explosion.TrackingMode.TARGET, .5f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return .7f;
	}

	public Vector2f getTextureCenter() {
		return v(3,3);
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Entity || other instanceof Missile && ((Missile)other).getOrigin() != origin)
			explode = true;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getTextureScaleFactor() {
		return .75f;
	}

	public int getBurstLength() {
		return Math.min(4, level);
	}

	public String getTexturePath() {
		return "pixmaps/blast.png";
	}

	public float getLaunchSpeed() {
		return 55;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1;
	}

	public float getReloadTime() {
		return 100;
	}
}
