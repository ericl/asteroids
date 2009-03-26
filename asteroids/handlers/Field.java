/*
 * Asteroids - APCS Final Project
 *
 * This source is provided under the terms of the BSD License.
 *
 * Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The authors' names may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package asteroids.handlers;

import java.awt.*;

import asteroids.bodies.*;

import asteroids.display.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

import static java.lang.Math.*;

/**
 * Responsible for asteroid field creation; closely coupled with ship handling.
 */
public class Field {
	protected Ship[] ships;
	protected World world;
	protected Display2 display;
	protected int ai_frequency = 50;
	protected Dimension dim;
	protected int count, score = -1;
	protected final static int BORDER = 300, BUF = 500;
	protected final static double MIN_DENSITY = 2e-4;
	protected static double D = 1;
	protected static final float INIT_I = 40, INIT_S = 1;
	protected float I = INIT_I, S = INIT_S; // speed of asteroids; scaling constant
	protected float rI = 1, rS = 1; // scalers for above
	public final static int HEX = 1, LARGE = 2, ROCKY = 3, ICEY = 4;
	public final static int[] ids = {HEX, LARGE, ROCKY, ICEY};
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
	 * @param	ships Ships inside the world.
	 */
	public Field(World w, Display2 d, int id, Ship ... ships) {
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
	public void setAIFrequency(int chance) {
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
		for (Ship ship : ships) {
			ship.reset();
			world.add(ship);
		}
		count = 0;
		score = -1;
	}

	/**
	 * Checks if the game is done.
	 * @return	True if the scenario should be restarted, false otherwise.
	 */
	public boolean done() {
		for (Ship ship : ships)
			if (ship.dead())
				return true;
		return false;
	}

	/**
	 * @return	Score.
	 */
	public int asteroids() {
		return done() ? score : count;
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
			if (density[i] < dim.getWidth()*dim.getHeight()*MIN_DENSITY*D) {
				if (oneIn(ai_frequency))
					world.add(newAI(targets[i].getPosition()));
				else
					world.add(newAsteroid(targets[i].getPosition()));
			}

		if (done() && score < 0)
			score = count;
	}

	public Body newAI(ROVector2f origin) {
		ComputerShip ai = new ComputerShip(world);
		ROVector2f vo = display.getOffscreenCoords(
			ai.getRadius(), BORDER, origin);
		ai.setPosition(vo.getX(), vo.getY());
		return ai;
	}

	/**
	 * @return	A new asteroid at some point.
	 */
	protected Body newAsteroid(ROVector2f origin) {
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

	/**
	 * Adjust asteroid attributes to make the game more difficult as time goes on.
	 */
	private void adjustForDifficulty(Asteroid rock) {
		float max = I + (float)Math.max(S*log10(count)*cbrt(count), 0);
		rock.setMaxVelocity(max,max);
		rock.adjustVelocity(v(range(-max,max), range(-max,max)));
	}

	/**
	 * @return	What scenario is being played.
	 */
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
