package asteroids.bodies;
import static asteroids.Util.*;
import static asteroids.bodies.PolyAsteroid.*;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class ArmorRecovery extends PowerUp implements Textured {
	
	protected static ROVector2f[] raw = { v(176,8), v(325,30), v(311,54), v(307,78), v(310,113), v(310,138), v(306,167), v(281,212), v(176,329), v(56,186), v(44,159), v(41,140), v(41,115), v(44,80), v(40,53), v(28,28), v(86,16) };
	protected static float RATIO = .05f;
	protected float radius;
	protected Vector2f centroid;

	public ArmorRecovery() {
		super(new Polygon(centralized(scaled(raw, RATIO))));
		AABox a = getShape().getBounds();
		radius = Math.max(a.getWidth(), a.getHeight()) / 2;
		centroid = new Polygon(raw).getCentroid();
	}

	public float getRadius() {
		return radius;
	}

	public float getRotation() {
		return 0;
	}

	public void up(Ship ship) {
		ship.setArmor(Ship.MAX);
	}

	public String getTexturePath() {
		return "pixmaps/armor2.png";
	}

	public Vector2f getTextureCenter() {
		return centroid;
	}

	public float getTextureScaleFactor() {
		return RATIO;
	}
}
