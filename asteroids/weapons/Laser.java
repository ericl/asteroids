/**
 * Weak, fast-firing laser.
 */

package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.Vector2f;
import static asteroids.Util.*;

public class Laser extends Weapon {
	private static float myRadius = 2;

	public Laser(World w, Body o) {
		super(new Circle(myRadius), 1, w, o);
	}

	public Laser duplicate() {
		Laser l = new Laser(world, origin);
		l.setLevel(getLevel());
		return l;
	}

	public Vector2f getTextureCenter() {
		return v(7,3);
	}

	public boolean isMaxed() {
		return level >= 3;
	}

	protected long getLifetime() {
		return 2000;
	}

	public float getDamage() {
		return .075f + .0375f * Math.min(3, level);
	}

	public float getReloadTime() {
		return 200;
	}

	public float getLaunchSpeed() {
		return 40 - 5 * Math.min(3, level);
	}

	public float getWeaponSpeed() {
		return 40 - 5 * Math.min(3, level);
	}

	public int getNum() {
		return 1 + Math.min(0, level);
	}

	public int getBurstLength() {
		return 15;
	}

	public Body getRemnant() {
		return new LaserExplosion(Explosion.TrackingMode.TARGET);
	}
	
	public List<Body> getFragments() {
		return null;
	}

	public String getTexturePath() {
		return "pixmaps/laser.png";
	}

	public float getTextureScaleFactor() {
		return .4f + Math.min(2, level) * .1f;
	}
	
	public float getRadius() {
		return myRadius;
	}
}
