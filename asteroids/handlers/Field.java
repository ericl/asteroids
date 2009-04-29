/**
 * Responsible for asteroid field creation; closely coupled with ship handling.
 */

package asteroids.handlers;

import java.awt.*;

import asteroids.AbstractGame;

import asteroids.bodies.*;

import asteroids.display.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.AbstractGame.Level.*;

import static asteroids.Util.*;

import static java.lang.Math.*;

public class Field {
	protected Entity[] ships;
	protected World world;
	protected Display display;
	protected double ai_frequency = .01;
	protected Dimension dim;
	protected int count, newcount;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected static double D = 1;
	protected static final float INIT_I = 40, INIT_S = 1;
	protected float I = INIT_I, S = INIT_S; // speed of asteroids; scaling constant

	/**
	 * @param	w	The world.
	 * @param	d	The display.
	 * @param	id	What type of field to be created.
	 * @param	ships Entitys inside the world.
	 */
	public Field(World w, Display d, Entity ... ships) {
		this.display = d;
		this.ships = ships;
		dim = display.getDimension();
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
			for (int i=1; i < AbstractGame.globalLevel.quantify(); i++)
				ship.upgradeWeapons();
			if (AbstractGame.globalLevel.quantify() > MEDIUM.quantify()) {
				Shield s = new Shield(ship, world);
				world.add(s);
			}
			world.add((Body)ship);
		}
		count = 1;
		newcount = 1;
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
			if (Math.random() < ai_frequency) {
				Body ai = newAI(targets[i].getPosition());
				if (ai != null)
					world.add(ai);
			}
		float[] density = new float[targets.length];
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			boolean outOfRange = true;
			for (int j=0; j < targets.length; j++)
				if (display.inViewFrom(targets[j].getPosition(),
						body.getPosition(), BORDER+BUF)) {
					if (body instanceof Visible) {
						if (((Visible)body).getRadius() > 15)
							density[j]++;
						else
							density[j] += .3f;
					}
					outOfRange = false;
				}
			if (outOfRange) {
				count++;
				world.remove(body);
			}
		}
		for (int i=0; i < density.length; i++)
			if (density[i] < dim.getWidth()*dim.getHeight()*MIN_DENSITY*D) {
				Body a = newAsteroid(targets[i].getPosition());
				if (a != null)
					world.add(a);
			}
	}

	public Body newAI(ROVector2f origin) {
		Body ret = null;
		Entity ai = null;
		switch (AbstractGame.globalLevel) {
			case START:
				ai = new Frigate(world, false);
				break;
			case EASY:
				ai = new Frigate(world, true);
				break;
			case MEDIUM:
				ai = new Ship(world, oneIn(2));
				break;
			case HARD:
				ai = new Jug(world);
				break;
			case BLUE:
				ai = new Terror(world);
				break;
			case SWARM:
			case DONE:
				ai = new Swarm(world);
				break;
			default:
				assert false;
		}
		if (ai != null)
			ret = ai;
		if (ret != null) {
			ROVector2f vo = display.getOffscreenCoords(((Visible)ret).getRadius(), BORDER, origin);
			ret.setPosition(vo.getX(), vo.getY());
		}
		return ret;
	}

	public ROVector2f getCenter() {
		return ships.length > 0 ? ships[0].getPosition() : v(0,0);
	}

	/**
	 * @return	A new asteroid at some point.
	 */
	protected Body newAsteroid(ROVector2f origin) {
		Body rock = null;
		switch (AbstractGame.globalLevel) {
			case START:
			case EASY:
				rock = new IceAsteroid(range(30,50));
				break;
			case SWARM:
			case MEDIUM:
			case HARD:
				rock = new BigAsteroid(range(30,50));
				break;
			case BLUE:
				rock = new HexAsteroid(range(30,50));
				break;
			case DONE:
				rock = new Swarm(world);
				break;
			default:
				assert false;
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
		if (AbstractGame.globalLevel == DONE) {
			newcount++;
			max += newcount / 33f;
		}
		rock.setMaxVelocity(max,max);
		rock.adjustVelocity(v(range(-max,max), range(-max,max)));
	}

	/**
	 * @return	Current state of scenario.
	 */
	public String toString() {
		if (AbstractGame.globalLevel == DONE)
			return "Out of levels!";
		return AbstractGame.globalLevel.toString() + " of " + (DONE.quantify() - 1);
	}
}
