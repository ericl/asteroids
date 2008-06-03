package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import java.awt.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Field implements Scenario {
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected int[] density;
	protected Ship[] ships;
	protected World world;
	protected Display display;
	protected Dimension dim;
	protected int count, score = -1;
	public static String[] ids = {"circles", "hex", "large",
	                              "basic", "rocky", "icey"};
	protected String id;

	public Field(World w, Display d, Ship ship, String id) {
		this.id = id;
		this.display = d;
		this.dim = d.getDimension();
		ships = new Ship[1];
		ships[0] = ship;
		density = new int[ships.length];
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id.equals(ids[i]))
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public Field(World w, Display d, Ship[] shiparray, String id) {
		this.display = d;
		this.id = id;
		this.dim = d.getDimension();
		ships = shiparray;
		density = new int[ships.length];
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
						if (display.inViewFrom(ships[j].getPosition(),
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
			if (density[i] < dim.getWidth()*dim.getHeight()*MIN_DENSITY)
				world.add(newAsteroid(ships[i].getPosition()));
		if (done() && score < 0) {
			for (Ship ship : ships)
				world.remove(ship);
			score = count;
		}
	}

	protected Asteroid newAsteroid(ROVector2f origin) {
		// difficulty increases with count
		Asteroid rock = null;
		if (id.equals("basic")) {
			switch ((int)(5*Math.random())) {
				case 1: rock = new HexAsteroid(range(20,30)); break;
				case 2: rock = new Rock2(range(20,30)); break;
				case 3: rock = new IceAsteroid(range(20,70)); break;
				default: rock = new CircleAsteroid(range(20,30)); break;
			}
			if (oneIn(200))
				rock = new CircleAsteroid(range(100,300));
		} else if (id.equals("large"))
			rock = new CircleAsteroid(oneIn(3) ? range(100,200) : range(10,20));
		else if (id.equals("circles"))
			rock = new CircleAsteroid(range(30,40));
		else if (id.equals("hex"))
			rock = new HexAsteroid(range(10,50));
		else if (id.equals("rocky"))
			rock = new Rock2(40);
		else if (id.equals("icey"))
			rock = new IceAsteroid(range(10,90));
		// workaround for rogue collisions
		rock.setMaxVelocity(10+count/10, 10+count/10);
		rock.adjustAngularVelocity((float)(2*Math.random()-1));
		ROVector2f vo = display.getOffscreenCoords(
			rock.getRadius(), BORDER, origin);
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(range(-count/20-10,count/20+10),
		                      range(count/-20-10,count/20+10)));
		return rock;
	}
}
