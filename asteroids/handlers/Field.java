package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import java.awt.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Field {
	protected int[] density;
	protected Ship[] ships;
	protected World world;
	protected Display display;
	protected Dimension dim;
	protected int count;
	public int id, score = -1;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected float I = 30; // initial speed of asteroids
	public final static int HEX = 1;
	public final static int LARGE = 2;
	public final static int ROCKY = 3;
	public final static int ICEY = 4;
	public final static int[] ids = {HEX, LARGE, ROCKY, ICEY};

	public Field(World w, Display d, Ship ship, int id) {
		this.id = id;
		this.display = d;
		this.dim = d.getDimension();
		ships = new Ship[1];
		ships[0] = ship;
		density = new int[ships.length];
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id == ids[i])
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public Field(World w, Display d, Ship[] shiparray, int id) {
		this.display = d;
		this.id = id;
		this.dim = d.getDimension();
		ships = shiparray;
		density = new int[ships.length];
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id == ids[i])
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public void setInitialSpeed(float speed) {
		I = speed;
	}

	public void start() {
		world.clear();
		for (Ship ship : ships) {
			ship.reset();
			world.add(ship);
		}
		count = 0;
		score = -1;
	}

	public boolean done() {
		for (Ship ship : ships)
			if (ship.dead())
				return true;
		return false;
	}

	public int score() {
		return done() ? score : count;
	}

	public void update() {
		for (int i=0; i < density.length; i++)
			density[i] = 0;
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
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
		for (int i=0; i < density.length; i++)
			if (density[i] < dim.getWidth()*dim.getHeight()*MIN_DENSITY)
			{
				world.add(newAsteroid(ships[i].getPosition()));
			}

		if (done() && score < 0) {
			for (Ship ship : ships)
				if (ship.canExplode())
					world.remove(ship);
			score = count;
		}
	}

	protected Asteroid newAsteroid(ROVector2f origin) {
		// difficulty increases with count
		Asteroid rock = null;
		switch (id) {
			case LARGE:
				int max = 175;
				if (oneIn(2))
					max	= 50;
				switch ((int)(Math.random()*3)) {
					case 0:
						rock = new BigAsteroid(range(10,max));
						break;
					case 1:
						rock = new HexAsteroid(range(10,max));
						break;
					default:
						rock = new IceAsteroid(range(10,max));
						break;
				}
				break;
			case HEX:
				rock = new HexAsteroid(oneIn(100) ? range(100,200) : range(30,50));
				break;
			case ROCKY:
				rock = new BigAsteroid(range(30,50));
				break;
			case ICEY:
				rock = new IceAsteroid(range(10,90));
				break;
		}
		// workaround for rogue collisions
		rock.setMaxVelocity(I+count/10, I+count/10);
		rock.adjustAngularVelocity((float)(1.5*Math.random()-.75));
		rock.setRotDamping(100);
		ROVector2f vo = display.getOffscreenCoords(
			rock.getRadius(), BORDER, origin);
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(range(-count/20-I,count/20+I),
		                      range(count/-20-I,count/20+I)));
		return rock;
	}

	public String toString() {
		switch (id) {
			case HEX: return "Hexagons";
			case LARGE: return "Large";
			case ROCKY: return "Rocky";
			case ICEY: return "Icey";
		}
		return "Unknown";
	}
}
