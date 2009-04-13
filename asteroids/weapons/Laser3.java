/**
 * Powerful, fast-moving blast.
 */

package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import static asteroids.Util.*;

public class Laser3 extends Weapon {
	private static float myRadius = 3;
	private boolean explode;

	public Laser3() {
		super(new Circle(myRadius), 100);
		setRestitution(1);
	}

	public Laser3 duplicate() {
		Laser3 l = new Laser3();
		l.setLevel(getLevel());
		return l;
	}

	public Body getRemnant() {
		return new LaserExplosion(Explosion.TrackingMode.TARGET, 2);
	}

	public List<Body> getFragments() {
		return null;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getDamage() {
		return 1.3f;
	}

	public Vector2f getTextureCenter() {
		return v(21,20);
	}

	public float getTextureScaleFactor() {
		return 1f;
	}

	public int getBurstLength() {
		return Math.min(4, level);
	}

	public String getTexturePath() {
		return "pixmaps/exp2/1.png";
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
		explode = !(other instanceof Weapon) || other instanceof Laser3;
	}

	public int getNum() {
		return 1 + Math.min(2, level) / 2;
	}

	public float getReloadTime() {
		return 1200;
	}
}
