/**
 * Responsible for asteroid field creation; closely coupled with ship handling.
 */

package asteroids.handlers;

import java.awt.*;

import asteroids.bodies.*;

import asteroids.display.*;

import asteroids.ai.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

import static java.lang.Math.*;

public class Field {
	protected Entity[] ships;
	protected World world;
	protected Display display;
	protected double ai_frequency = .01;
	protected Dimension dim;
	protected int count;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected static double D = 1;
	protected static final float INIT_I = 40, INIT_S = 1;
	protected float I = INIT_I, S = INIT_S; // speed of asteroids; scaling constant
	protected float rI = 1, rS = 1; // scalers for above
	public final static int HEX = 1, ROCKY = 3, ICEY = 4;
	public final static int[] ids = {HEX, ICEY, ROCKY};
	private int id;

	/**
	 * @return	The unique identifier of the field.
	 */
	public int getID() {
		return id;
	}

	/**
	 * @param	w	The world.
	 * @param	d	The display.
	 * @param	id	What type of field to be created.
	 * @param	ships Entitys inside the world.
	 */
	public Field(World w, Display d, int id, Entity ... ships) {
		this.display = d;
		this.id = id;
		this.ships = ships;
		dim = display.getDimension();
		boolean ok = false;
		for (int i=0; i < ids.length; i++)
			if (id == ids[i])
				ok = true;
		if (!ok)
			throw new IllegalArgumentException("Unknown id " + id);
		world = w;
	}

	/**
	 * Sets the density of the field in which how many asteroids are inside the field.
	 * @param	density	The density of which to set the star field to.
	 */
	public void setDensity(double density) {
		D = density;
	}

	/**
	 * oneIn(chance)
	 */
	public void setAIFrequency(double chance) {
		ai_frequency = chance;
	}

	/**
	 * Set ratio that scales initial speed of generated asteroids.
	 * @param	ratio	The scale ratio, where 1.0 = 100%
	 */
	public void setSpeedRatio(float ratio) {
		I = INIT_I*ratio;
	}

	/**
	 * Set ratio that scales the speed increase of generated asteroids.
	 * @param	ratio	The scale ratio, where 1.0 = 100%
	 */
	public void setScalingRatio(float ratio) {
		S = INIT_S*ratio;
	}

	/**
	 * Starts the field world.
	 */
	public void start() {
		world.clear();
		for (Entity ship : ships) {
			ship.reset();
			world.add((Body)ship);
		}
		count = 0;
	}

	/**
	 * Checks if the game is done.
	 * @return	True if the scenario should be restarted, false otherwise.
	 */
	public boolean done() {
		for (Entity ship : ships)
			if (ship.dead())
				return true;
		return false;
	}

	/**
	 * @return	Score.
	 */
	public int asteroids() {
		return count;
	}

	/**
	 * @return	List of targets to be surrounded by asteroids.
	 */
	protected Visible[] getTargets() {
		return ships;
	}

	/**
	 * Updates the field in which adding new asteroids and removing other unneeded things.
	 */
	public void update() {
		Visible[] targets = getTargets();
		for (int i=0; i < targets.length; i++)
			if (Math.random() < ai_frequency)
				world.add(newAI(targets[i].getPosition()));
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
	}

	public Body newAI(ROVector2f origin) {
		Body ai = new ComputerShip(world);
		ROVector2f vo = display.getOffscreenCoords(((Visible)ai).getRadius(), BORDER, origin);
		ai.setPosition(vo.getX(), vo.getY());
		return ai;
	}

	public ROVector2f getCenter() {
		return ships.length > 0 ? ships[0].getPosition() : v(0,0);
	}

	/**
	 * @return	A new asteroid at some point.
	 */
	protected Body newAsteroid(ROVector2f origin) {
		// difficulty increases with count
		Body rock = null;
		switch (id) {
			case ROCKY:
				rock = new BigAsteroid(oneIn(25) ? range(100,150) : range(30,50));
				break;
			case ICEY:
				rock = new IceAsteroid(oneIn(20) ? range(100,175) : range(30,50));
				break;
			case HEX:
				rock = new HexAsteroid(oneIn(15) ? range(100,200) : range(30,50));
				break;
		}
		adjustForDifficulty(rock);
		rock.adjustAngularVelocity((float)(1.5*random()-.75));
		ROVector2f vo = display.getOffscreenCoords(
			((Visible)rock).getRadius(), BORDER, origin);
		rock.setPosition(vo.getX(), vo.getY());
		return rock;
	}

	/**
	 * Adjust asteroid attributes to make the game more difficult as time goes on.
	 */
	private void adjustForDifficulty(Body rock) {
		float max = I + (float)Math.max(S*log10(count)*cbrt(count), 0);
		rock.setMaxVelocity(max,max);
		rock.adjustVelocity(v(range(-max,max), range(-max,max)));
	}

	/**
	 * @return	What scenario is being played.
	 */
	public String toString() {
		switch (id) {
			case HEX: return "\"Hexagons\"";
			case ROCKY: return "\"Rocky\"";
			case ICEY: return "\"Ice\"";
			default: return "Unknown";
		}
	}
}
