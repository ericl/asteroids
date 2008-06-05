package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import java.awt.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Field implements Scenario {
	protected int[] density;
	protected Ship[] ships;
	protected World world;
	protected Display display;
	protected Dimension dim;
	protected int count, score = -1;
	protected int id;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
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
				rock = new BigAsteroid(oneIn(3) ? range(100,175) : range(10,75));
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
		rock.setMaxVelocity(10+count/10, 10+count/10);
		rock.adjustAngularVelocity((float)(1.5*Math.random()-.75));
		ROVector2f vo = display.getOffscreenCoords(
			rock.getRadius(), BORDER, origin);
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(range(-count/20-10,count/20+10),
		                      range(count/-20-10,count/20+10)));
		return rock;
	}
	
	protected PowerUp newPowerUp(ROVector2f origin) {
		// difficulty increases with count
		PowerUp h = new ArmorRecovery(10, 1);
		// workaround for rogue collisions
		h.setMaxVelocity(10+count/10, 10+count/10);
		h.adjustAngularVelocity((float)(2*Math.random()-1));
		ROVector2f vo = display.getOffscreenCoords(
			h.getRadius(), BORDER, origin);
		h.setPosition(vo.getX(), vo.getY());
		h.adjustVelocity(v(range(-count/20-10,count/20+10),
		                      range(count/-20-10,count/20+10)));
		return h;
	}
}