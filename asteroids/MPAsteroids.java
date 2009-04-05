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

package asteroids;

import java.awt.*;

import java.awt.event.*;

import asteroids.bodies.*;

import asteroids.display.Display;

import asteroids.handlers.*;

import net.phys2d.math.ROVector2f;

import static asteroids.Util.*;

/**
 * Two-player, unscored game where the players compete against each other.
 */
public class MPAsteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500, BASE_HEIGHT = 500;
	private static final int NUM_PLAYERS = 2;
	protected Field scenario;
	protected Pointer[] pointer = new Pointer[NUM_PLAYERS];
	protected Ship[] ships = new Ship[NUM_PLAYERS];
	protected StarField k;
	protected boolean restart;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids();
		game.mainLoop();
	}

	protected Display makeDisplay() {
		frame.setLocationByPlatform(true);
		GridLayout layout = new GridLayout(NUM_PLAYERS/2, NUM_PLAYERS, 2, 2);
		frame.setLayout(layout);
		Canvas[] canvases = new Canvas[NUM_PLAYERS];
		for (int i=0; i < NUM_PLAYERS; i++) {
			canvases[i] = new Canvas();
			frame.add(canvases[i]);
			canvases[i].setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
		}
		frame.setSize(layout.preferredLayoutSize(frame));
		return new Display(frame, dim, canvases);
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		for (int i=2; i < NUM_PLAYERS; i++) {
			final int foo = i;
			frame.addKeyListener(ships[i] = new ComputerShip(world) {
				public boolean isCloaked() {
					return false;
				}

				public String toString() {
					return "COMPUTER";
				}

				public void keyPressed(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {}

				public void reset() {
					super.reset();
					setPosition(400*foo, 200*(foo % 2));
				}	
			});
		}
		if (NUM_PLAYERS > 1)
			frame.addKeyListener(ships[1] = new ComputerShip(world, true) {
				public boolean isCloaked() {
					return false;
				}

				public void reset() {
					super.reset();
					setPosition(400, 200);
				}
			});
		frame.addKeyListener(ships[0] = new ComputerShip(world, true) {
			public boolean isCloaked() {
				return false;
			}

			public String toString() {
				return "Controls: wasd, `, 1";
			}

			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': torque = -8e-5f; notifyInput(); break;
					case 'd': torque = 8e-5f; notifyInput(); break;
					case 'w': accel = 30*A; notifyInput(); break;
					case 's': accel = -15*A; notifyInput(); break;
					case '`': fire = true; notifyInput(); break;
					case KeyEvent.VK_1: launch = true; notifyInput(); break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a':
					case 'd': torque = 0; notifyInput(); break;
					case 'w':
					case 's': accel = 0; notifyInput(); break;
					case '`': fire = false; notifyInput(); break;
					case KeyEvent.VK_1: launch = false; notifyInput(); break;
				}
			}
		});
		Ship.setMax(4);
		Ship.setSpeed(.25f);
		for (int i=0; i < NUM_PLAYERS; i++) {
			Explodable[] targets = new Explodable[NUM_PLAYERS-1];
			int x = 0;
			for (int j=0; j < NUM_PLAYERS; j++) {
				if (j != i)
					targets[x++] = ships[j];
			}
			pointer[i] = new Pointer(ships[i], display, targets);
		}
		display.setBackground("pixmaps/background2.jpg");
		k = new StarField(display);
		newGame();
	}

	protected void update() {
		if (restart) {
			newGame();
			restart = false;
		}
		scenario.update();
		ROVector2f[] centers = new ROVector2f[NUM_PLAYERS];
		for (int i=0; i < NUM_PLAYERS; i++)
			centers[i] = ships[i].getPosition();
		display.setCenter(centers);
	}

	protected void preWorld() {
		k.starField();
		Graphics2D[] g2ds = display.getGraphics();
		for (int i=0; i < NUM_PLAYERS; i++)
			pointer[i].drawTo(g2ds[i]);
	}

	protected void postWorld() {
		Graphics2D[] g2ds = display.getGraphics();
		for (int i=0; i < NUM_PLAYERS; i++) {
			if (ships[i].dead()) {
				g2ds[i].setColor(COLOR);
				g2ds[i].setFont(FONT_NORMAL);
				g2ds[i].drawString(RESTART_MSG,
					centerX(FONT_NORMAL, RESTART_MSG, g2ds[i]), display.h(0)/2-5);
			}
			shipStatus(g2ds[i], ships[i]);
		}
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': restart = true; break;
		}
	}

	public void newGame() {
		k.init();
		int id = Field.ids[(int)range(0, Field.ids.length)];
		scenario = new Field(world, display, id, ships);
		scenario.setAIFrequency(0);
		scenario.setSpeedRatio(.25f);
		scenario.setScalingRatio(.25f);
		scenario.setDensity(.40f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(ship.getColor());
		g2d.drawString("Armor: " + hull,
			display.w(-110), display.h(-59));
		g2d.setColor(COLOR);
		g2d.drawString("Missiles: " + ship.numMissiles(),
			display.w(-110), display.h(-39));
		g2d.drawString("Deaths: " + ship.deaths,
			display.w(-110), display.h(-19));
		g2d.drawString(ship.toString(), 10, 20);
	}
}
