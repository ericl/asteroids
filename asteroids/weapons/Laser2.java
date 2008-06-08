package asteroids.weapons;
import static asteroids.Util.*;
import java.util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Laser2 extends Weapon {
	static float myRadius = 2;

	public Laser2() {
		super(new Circle(myRadius), 100);
		setRestitution(1);
	}

	public Body getRemnant() {
		return new LargeExplosion();
	}

	public List<Body> getFragments() {
		return null;
	}

	public float getDamage() {
		return .6f;
	}

	public Vector2f getTextureCenter() {
		return v(3,3);
	}

	public float getTextureScaleFactor() {
		return .9f;
	}

	public int getBurstLength() {
		return 5;
	}

	public String getTexturePath() {
		return "pixmaps/blast.png";
	}

	public float getSpeed() {
		return 50;
	}

	public float getRadius() {
		return myRadius;
	}

	public int getNum() {
		return 1+level;
	}

	public float getReloadTime() {
		return 700;
	}
}
