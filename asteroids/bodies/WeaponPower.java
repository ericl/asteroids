package asteroids.bodies;
import static asteroids.Util.*;
import static asteroids.bodies.PolyAsteroid.*;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class WeaponPower extends PowerUp implements Textured {
	
	protected static ROVector2f[] raw = {v(11,1),v(20,10),v(11,20),v(1,11)};
	protected static float RATIO = 1f;
	protected float radius;
	protected Vector2f centroid;

	public WeaponPower() {
		super(new Polygon(centralized(scaled(raw, RATIO))));
		AABox a = getShape().getBounds();
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	public float getRotation() {
		return 0;
	}

	public float getRadius() {
		return radius;
	}

	public void up(Ship ship) {
		ship.weapons.upgrade();
	}

	public String getTexturePath() {
		return "pixmaps/dialog-question.png";
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}

	public float getTextureScaleFactor() {
		return RATIO;
	}
}
