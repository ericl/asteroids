package asteroids.weapons;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.Vector2f;
import static asteroids.Util.*;

public class Laser extends Weapon {
	private float myRadius;

	public Laser() {
		super((new Circle(2)),5,1f);
		myRadius = 2;
	}

	public Vector2f getTextureCenter() {
		return v(7,3);
	}

	public boolean canExplode() {
		return true;
	}

	public Body getRemnant() {
		Explosion e = new LaserExplosion();
		e.setEnabled(false);
		return e;
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
