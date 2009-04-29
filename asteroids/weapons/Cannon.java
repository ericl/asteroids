/**
 * Powerful, fast-moving blast.
 */

package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import static asteroids.Util.*;

public class Cannon extends Weapon {
	private static float myRadius = 3;
	private boolean explode;

	public Cannon() {
		super(new Circle(myRadius), 100);
		addBit(BIT_SHIELD_PENETRATING);
		setRestitution(1);
	}

	public Cannon duplicate() {
		return new Cannon();
	}

	public Body getRemnant() {
		return new LargeExplosion(Explosion.TrackingMode.TARGET, .6f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getDamage() {
		return .33f;
	}

	public Vector2f getTextureCenter() {
		return v(6.5,6.5);
	}

	public float getTextureScaleFactor() {
		return .80f;
	}

	public String getTexturePath() {
		return "pixmaps/cannon.png";
	}

	public float getLaunchSpeed() {
		return 50;
	}

	public float getWeaponSpeed() {
		return 50;
	}

	public float getRadius() {
		return myRadius;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		explode = !(other instanceof Weapon) || other instanceof Cannon || other instanceof Missile || other instanceof Laser3 || other instanceof Laser2;
	}

	public int getNum() {
		return 1;
	}

	public float getReloadTime() {
		return 333;
	}
}
