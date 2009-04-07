package asteroids.weapons;

import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import asteroids.handlers.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;

public class Laser2 extends Weapon {
	private static float myRadius = 2;
	private boolean thrust, explode;
	private float myError = range(-3e-1, 3e-1);
	private float damage;
	private int steps;

	public Laser2() {
		super(new Circle(myRadius), 1);
		setRestitution(1);
		setRotDamping(5);
	}

	public Laser2(int level) {
		super(new Circle(myRadius), 1);
		setRestitution(1);
		setRotDamping(5);
		setLevel(level);
	}

	public Laser2 duplicate() {
		Laser2 l = new Laser2();
		l.setLevel(getLevel());
		return l;
	}

	public boolean canExplode() {
		return explode || damage > .2;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		explode = !(other instanceof Weapon) || other instanceof Laser3 || other instanceof Missile;
		damage += Exploder.getDamage(e, this);
	}

	public Body getRemnant() {
		return new LargeExplosion(.75f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return .6f;
	}

	public void endFrame() {
		super.endFrame();
		if (steps++ > 75) {
			thrust = true;
			if (steps > 500)
				thrust = false;
		}
		Vector2f dir = direction(getRotation());
		float accel = 20;
		float v = getVelocity().length();
		setDamping(v < 50 ? 0 : v < 100 ? .1f : .5f);
		if (thrust) {
			addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
			if (steps < 200) {
				float delta = origin.getRotation() - getRotation();
				float sign = sign(delta);
				delta = sign*(float)Math.min(.05, Math.abs(delta));
				setRotation(getRotation() + delta);
			} else
				adjustAngularVelocity(myError);
			adjustAngularVelocity(myError);
		}
	}

	public Vector2f getTextureCenter() {
		return v(8,8);
	}

	public float getTextureScaleFactor() {
		return .75f;
	}

	public int getBurstLength() {
		return 5;
	}

	public String getTexturePath() {
		return thrust ?  "pixmaps/rocket-t.png" : "pixmaps/rocket.png";
	}

	public float getLaunchSpeed() {
		return 30;
	}

	public float getWeaponSpeed() {
		return 50;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1 + Math.min(2, level);
	}

	public float getReloadTime() {
		return 1100;
	}
}
