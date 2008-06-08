package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import static java.lang.Math.*;
import java.awt.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Field {
	protected Ship[] ships;
	protected World world;
	protected Display display;
	protected Dimension dim;
	protected int count;
	protected int score = -1;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected static double D = 1;
	protected float I = 30, S = 2; // initial speed of asteroids; scaling constant
	public final static int HEX = 1;
	public final static int LARGE = 2;
	public final static int ROCKY = 3;
	public final static int ICEY = 4;
	public final static int[] ids = {HEX, LARGE, ROCKY, ICEY};
	private int id;

	public Field(World w, Display d, Ship ship, int id) {
		this.id = id;
		this.display = d;
		this.dim = d.getDimension();
		ships = new Ship[1];
		ships[0] = ship;
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id == ids[i])
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public int getID() {
		return id;
	}

	public Field(World w, Display d, Ship[] shiparray, int id) {
		this.display = d;
		this.id = id;
		this.dim = d.getDimension();
		ships = shiparray;
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id == ids[i])
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	public void setDensity(double density) {
		D = density;
	}

	public void setInitialSpeed(float speed) {
		I = speed;
	}

	public void setScalingConstant(float scale) {
		S = scale;
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

	protected Visible[] getTargets() {
		return ships;
	}

	public void update() {
		Visible[] targets = getTargets();
		int[] density = new int[targets.length];
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			boolean outOfRange = true;
			for (int j=0; j < targets.length; j++)
				if (display.inViewFrom(targets[j].getPosition(),
						body.getPosition(), BORDER+BUF)) {
					if (body instanceof Visible && ((Visible)body).getRadius() > 15)
						density[j]++;
					outOfRange = false;
				}
			if (outOfRange) {
				count++;
				world.remove(body);
			}
		}
		for (int i=0; i < density.length; i++)
			if (density[i] < dim.getWidth()*dim.getHeight()*MIN_DENSITY*D)
				world.add(newAsteroid(targets[i].getPosition()));

		if (done() && score < 0)
			score = count;
	}

	protected Asteroid newAsteroid(ROVector2f origin) {
		// difficulty increases with count
		Asteroid rock = null;
		switch (id) {
			case LARGE:
				int max = 175;
				if (oneIn(2))
					max	= 50;
				switch ((int)(random()*3)) {
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
		adjustForDifficulty(rock);
		rock.adjustAngularVelocity((float)(1.5*random()-.75));
		ROVector2f vo = display.getOffscreenCoords(
			rock.getRadius(), BORDER, origin);
		rock.setPosition(vo.getX(), vo.getY());
		return rock;
	}

	private void adjustForDifficulty(Asteroid rock) {
		// workaround for rogue collisions
		rock.setMaxVelocity(I+S*(float)sqrt(count), I+S*(float)sqrt(count));
		rock.adjustVelocity(v(range(-S*sqrt(count)-I,S*sqrt(count)+I),
		                      range(-S*sqrt(count)-I,S*sqrt(count)+I)));
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
