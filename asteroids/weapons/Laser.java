package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.Vector2f;
import static asteroids.Util.*;

public class Laser extends Weapon {
	private static float myRadius = 2;

	public Laser() {
		super(new Circle(myRadius));
	}

	public Vector2f getTextureCenter() {
		return v(7,3);
	}

	public boolean canExplode() {
		return true;
	}

	public float getDamage() {
		return .05f;
	}

	public float getReloadTime() {
		return 100;
	}

	public float getSpeed() {
		return 200;
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
		return myRadius / 2;
	}
	
	public float getRadius() {
		return myRadius;
	}
}
