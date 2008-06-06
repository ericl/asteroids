package asteroids.bodies;
import static asteroids.Util.*;
import static asteroids.bodies.PolyAsteroid.*;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.ROVector2f;

public class Invincibility extends PowerUp implements Textured {
	private static ROVector2f[] raw = { v(111,23), v(145,78), v(205,90), v(162,137), v(171,199), v(111,174), v(54,201), v(62,139), v(19,92), v(79,78)};
	private static float RATIO = .1f;
	private Vector2f centroid;
	private float radius;
	private static int INVINCIBLE_TIME = 20000;
	private static int WARNING_TIME = 4000;

	public Invincibility() {
		super(new Polygon(centralized(scaled(raw, RATIO))));
		AABox a = getShape().getBounds();
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}
	
	public float getRadius() {
		return radius;
	}

	public float getTextureScaleFactor() {
		return RATIO;
	}
	
	public void up(Ship ship) {
		if (!ship.isInvincible())
			ship.gainInvincibility(INVINCIBLE_TIME, WARNING_TIME);
	}

	public String getTexturePath() {
		return "pixmaps/invincibility.png";
	}
}
