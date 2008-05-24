package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Field implements Scenario {
	protected final int BORDER = 300, BUF = 500;
	protected final int MIN_DENSITY = 30;
	protected final Vector2f dim;
	protected Integer[] density;
	protected Ship[] ships;
	protected World world;
	protected int count, score = -1;
	public static String[] ids = {"circles", "hex", "large", "basic", "rocky"};
	protected String id;

	public Field(World w, Vector2f wxh, Ship ship, String id) {
		this.id = id;
		ships = new Ship[1];
		ships[0] = ship;
		density = new Integer[ships.length];
		dim = wxh;
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id.equals(ids[i]))
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public Field(World w, Vector2f wxh, Ship[] shiparray, String id) {
		this.id = id;
		ships = shiparray;
		density = new Integer[ships.length];
		dim = wxh;
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id.equals(ids[i]))
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public void start() {
		synchronized (world) {
			world.clear();
			for (Ship ship : ships) {
				ship.reset();
				world.add(ship);
			}
			count = 0;
			score = -1;
		}
	}

	public boolean done() {
		for (Ship ship : ships)
			if (ship.canExplode())
				return true;
		return false;
	}

	public int score() {
		return done() ? score : count;
	}

	public void update() {
		for (int i=0; i < density.length; i++)
			density[i] = 0;
		synchronized (world) {
			BodyList bodies = world.getBodies();
			for (int i=0; i < bodies.size(); i++) {
				Body body = bodies.get(i);
				if (body instanceof Asteroid) {
					boolean outOfRange = true;
					for (int j=0; j < ships.length; j++)
						if (isVisible(ships[j].getPosition(), dim,
								body.getPosition(), BORDER+BUF)) {
							density[j]++;
							outOfRange = false;
						}
					if (outOfRange) {
						count++;
						world.remove(body);
					}
				}
			}
		}
		for (int i=0; i < density.length; i++)
			if (density[i] < MIN_DENSITY)
				world.add(newAsteroid(ships[i].getPosition()));
		if (done() && score < 0) {
			for (Ship ship : ships)
				world.remove(ship);
			score = count;
		}
	}

	protected Asteroid newAsteroid(ROVector2f origin) {
		// difficulty increases with count
		float vx = (float)((10+count/100)*(5 - Math.random()*10));
		float vy = (float)((10+count/100)*(5 - Math.random()*10));
		Asteroid rock = null;
		if (id.equals("basic")) {
			switch ((int)(5*Math.random())) {
				case 1: rock = new HexAsteroid(range(20,30)); break;
				case 2: rock = new Rock2(range(20,30)); break;
				default: rock = new CircleAsteroid(range(20,30)); break;
			}
			if (oneIn(200))
				rock = new CircleAsteroid(range(100,300));
		} else if (id.equals("circles"))
			rock = new CircleAsteroid(range(30,40));
		else if (id.equals("large"))
			rock = new CircleAsteroid(range(100,200));
		else if (id.equals("hex"))
			rock = new HexAsteroid(range(10,50));
		else if (id.equals("rocky"))
			rock = new Rock2(40);
		// workaround for rogue collisions
		rock.setMaxVelocity(10+count/10, 10+count/10);
		rock.adjustAngularVelocity((float)(2*Math.random()-1));
		ROVector2f vo = getOffscreenCoords(rock.getRadius(), origin);
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(range(-count/20-10,count/20+10),
		                      range(count/-20-10,count/20+10)));
		return rock;
	}

	/**
	 * Get offscreen coords for a shape of radius r.
	 */
	protected ROVector2f getOffscreenCoords(float r, ROVector2f target) {
		ROVector2f v = target;
		ROVector2f center = MathUtil.scale(dim, .5f);
		boolean failed = true;
		while (failed) {
			float x = range(-BORDER-dim.getX(), BORDER+dim.getX());
			float y = range(-BORDER-dim.getY(), BORDER+dim.getY());
			v = MathUtil.sub(target, v(-x-r, -y-r));
			failed = false;
			for (Ship ship : ships)
				if (isVisible(MathUtil.sub(ship.getPosition(), center),dim,v,r)) {
					failed = true;
					break;
				}
		}
		return v;
	}
}
