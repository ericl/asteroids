package asteroids.handlers;
import asteroids.bodies.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

// controls the world for a while until goal is complete
// it would be nice if it read the scenario id and attributes
// off a config file - right now this class is "working", bu
// not as intended
public class Scenario {
	private World world;
	private Ship ship;
	private final int WIDTH = 500, HEIGHT = 500;
	private final int BORDER = 300, BUF = 500;
	private final int NUMROCKS = 50;
	private int count, score = -1;
	private float xo, yo;
	public static String[] ids = {"circles", "hex", "large", "basic", "rocky"};
	private String id;

	public Scenario(World w, Ship s, String id) {
		this.id = id;
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id.equals(ids[i]))
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
		ship = s;
	}

	public void start() {
		synchronized (world) {
			world.clear();
			ship.reset();
			world.add(ship);
			count = 0;
			score = -1;
		}
	}

	public boolean done() {
		return ship.canExplode();
	}

	public int score() {
		return done() ? score : count;
	}

	public void update() {
		xo = ship.getPosition().getX() - (float)WIDTH/2;
		yo = ship.getPosition().getY() - (float)HEIGHT/2;
		double xmax = xo + WIDTH + BORDER + BUF;
		double xmin = xo - BORDER - BUF;
		double ymax = yo + HEIGHT + BORDER + BUF;
		double ymin = yo - BORDER - BUF;
		synchronized (world) {
			BodyList bodies = world.getBodies();
			for (int i=0; i < bodies.size(); i++) {
				Body body = bodies.get(i);
				double x = body.getPosition().getX();
				double y = body.getPosition().getY();
				if (x > xmax || x < xmin || y > ymax || y < ymin) {
					count++;
					world.remove(body);
				}
			}
		}
		if (world.getBodies().size() <= NUMROCKS)
			world.add(newAsteroid());
		if (done() && score < 0)
			score = count;
	}

	protected Asteroid newAsteroid() {
		// difficulty increases with count
		float vx = (float)((1+count/100)*(5 - Math.random()*10));
		float vy = (float)((1+count/100)*(5 - Math.random()*10));
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
		rock.setMaxVelocity(count/10, count/10);
		rock.adjustAngularVelocity((float)(2*Math.random()-1));
		Vector2f vo = getOffscreenCoords(rock.getRadius());
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(vx, vy));
		return rock;
	}

	/**
	 * Get offscreen coords for a shape of radius r.
	 */
	protected Vector2f getOffscreenCoords(float r) {
		float x = 1, y = 1;
		// this is centered about the screen origin
		while(onScreen(v(x,y),r)) {
			x = (float)(Math.random()*2*(WIDTH + BORDER) - WIDTH - BORDER);
			y = (float)(Math.random()*2*(HEIGHT + BORDER) - HEIGHT - BORDER);
		}
		return v(x+xo+WIDTH/2+r,y+yo+HEIGHT/2+r);
	}

	// precondition: v is absolute vector from display origin
	protected boolean onScreen(ROVector2f v, float r) {
		float w2 = (WIDTH/2 + r);
		float h2 = (HEIGHT/2 + r);
		float x = v.getX();
		float y = v.getY();
		return x > -w2-r && x < w2 && y > -h2-r && y < h2;
	}
}
