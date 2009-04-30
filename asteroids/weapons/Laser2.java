package asteroids.weapons;

import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class Laser2 extends Weapon implements Heavy {
	private static float myRadius = 2;
	private boolean thrust, explode;
	private float xmax, ymax;
	private float damage;
	private int steps;

	public Laser2() {
		super(new Circle(myRadius), 10);
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

	public boolean isMaxed() {
		return level >= 5;
	}

	public boolean canExplode() {
		return explode || damage > .2;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Heavy || !(other instanceof Weapon) && other.getMass() > 100)
			explode = true;
		damage += Exploder.getDamage(e, this);
	}

	public Body getRemnant() {
		return new LargeExplosion(Explosion.TrackingMode.TARGET, .70f);
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
		float accel = 10;
		if (thrust) {
			addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
			if (steps < 150) {
				xmax = Math.max(xmax, Math.abs(getVelocity().getX()));
				ymax = Math.max(ymax, Math.abs(getVelocity().getY()));
			} else
				setMaxVelocity(Math.max(40, xmax), Math.max(40, ymax));
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

	public int getNum() {
		return 1 + Math.min(1, level/3);
	}

	public float getWeaponSpeed() {
		return 50;
	}

	public float getRadius() {
		return myRadius;
	}

	public float getReloadTime() {
		return 900 - 100 * Math.min(5, level);
	}
}
