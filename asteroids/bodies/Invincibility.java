package asteroids.bodies;
import static asteroids.Util.*;
import static asteroids.bodies.PolyAsteroid.*;
import asteroids.display.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.ROVector2f;
import java.util.*;

public class Invincibility extends PowerUp implements Textured {
	private static ROVector2f[] raw = { v(111,23), v(145,78), v(205,90), v(162,137), v(171,199), v(111,174), v(54,201), v(62,139), v(19,92), v(79,78)};
	private static float RATIO = .1f;
	private Vector2f centroid;
	private float radius;
	private static Set<Ship> running = new HashSet<Ship>();
	private static long INVINCIBLE_TIME = 15000;

	private class ITimer extends Thread {
		private Ship ship;
		public ITimer(Ship s) {
			ship = s;
		}
		public void run() {
			ship.setInvincible(true);
			System.out.println("START SLEEP");
			try {
				Thread.sleep(INVINCIBLE_TIME);
			} catch (InterruptedException e) {}
			System.out.println("END SLEEP");
			ship.setInvincible(false);
			running.remove(ship);
		}
	}

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
		if (!running.contains(ship)) {
			running.add(ship);
			ITimer t = new ITimer(ship);
			t.start();
		}
	}

	public String getTexturePath() {
		return "pixmaps/invincibility.png";
	}
}
