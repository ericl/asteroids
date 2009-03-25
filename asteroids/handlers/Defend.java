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
import net.phys2d.raw.*;
import asteroids.bodies.*;
import asteroids.display.*;

/**
 * Field that provides a central object to be defended.
 */
public class Defend extends Field {
	private Europa europa;
	public final static int DEFEND = 5;

	/**
	 * Constructs a world where the asteroids are set to just rocky and a Europa.
	 * @param	w	The world.
	 * @param	d	The display.
	 * @param	ship	The ship inside the world.
	 */
	public Defend(World w, Display2 d, Ship ship) {
		super(w, d, Field.ROCKY, ship);
	}
	
	/**
	 * @return	Europa.
	 */
	public Europa getObject() {
		return europa;	
	}
	
	/**
	 * Overrides the 'real' id to provide a unique high score identifier.
	 *
	 * @return	The mission of this type of game.
	 */
	public int getID() {
		return DEFEND;
	}

	/**
	 * Starts the world in motion.
	 */
	public void start() {
		world.clear();
		europa = new Europa();	
		world.add(europa);
		for (int i=0; i < ships.length; i++) {
			ships[i].reset();
			ships[i].addExcludedBody(europa);
			world.add(ships[i]);
		}
		count = 0;
		score = -1;
	}

	/**
	 * @return	An array of Visible targets that should be surrounded by asteroids.
	 */
	protected Visible[] getTargets() {
		Visible[] targets = new Visible[ships.length+1];
		System.arraycopy(ships, 0, targets, 0, ships.length);
		targets[targets.length-1] = europa;
		return targets;
	}

	/**
	 * Updates the defend game logic.
	 */
	public void update() {
		if (europa.canExplode() && !ships[0].canExplode())
			display.setCenter(europa.getPosition());
		super.update();
	}

	/**
	 * @return	A boolean reflecting if the game is done.
	 */
	public boolean done() {
		return super.done() || europa.canExplode();
	}
}