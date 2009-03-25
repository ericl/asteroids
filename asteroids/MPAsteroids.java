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
import java.awt.GridLayout;

import java.awt.event.*;

import asteroids.bodies.*;

import asteroids.display.Display2;

import asteroids.handlers.*;

import static asteroids.Util.*;

/**
 * Two-player, unscored game where the players compete against each other.
 */
public class MPAsteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500, BASE_HEIGHT = 500;
	protected Field scenario;
	protected Pointer pLeft, pRight;
	protected Ship[] ships = new Ship[2];
	protected StarField k;
	protected boolean restart;
	protected final Ship ship1, ship2;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids();
		game.mainLoop();
	}

	protected Display2 makeDisplay() {
		frame.setLocationByPlatform(true);
		GridLayout layout = new GridLayout();
		layout.setHgap(2);
		frame.setLayout(layout);
		Canvas a = new Canvas(), b = new Canvas();
		frame.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		frame.add(a);
		frame.add(b);
		return new Display2(frame, dim, a, b);
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		frame.addKeyListener(ship2 = new ComputerShip(world));
		frame.addKeyListener(ship1 = new ComputerShip(world) {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': torque = -8e-5f; notifyInput(); break;
					case 'd': torque = 8e-5f; notifyInput(); break;
					case 'w': accel = 30*A; notifyInput(); break;
					case 's': accel = -15*A; notifyInput(); break;
					case '`': fire = true; notifyInput(); break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a':
					case 'd': torque = 0; notifyInput(); break;
					case 'w':
					case 's': accel = 0; notifyInput(); break;
					case '`': fire = false; notifyInput(); break;
				}
			}

			public void reset() {
				super.reset();
				setPosition(-150,0);
			}	
		});
		ships[0] = ship1;
		ships[1] = ship2;
		Ship.setMax(4);
		Ship.setSpeed(.33f);
		pLeft = new Pointer(ship1, display, ship2);
		pRight = new Pointer(ship2, display, ship1);
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
		display.setCenter(ship1.getPosition(), ship2.getPosition());
	}

	protected void preWorld() {
		k.starField();
		Graphics2D[] g2ds = display.getGraphics();
		pLeft.drawTo(g2ds[0]);
		pRight.drawTo(g2ds[1]);
	}

	protected void postWorld() {
		Graphics2D[] g2ds = display.getGraphics();
		for (int i=0; i < g2ds.length; i++) {
			if (ships[i].dead()) {
				g2ds[i].setColor(COLOR);
				g2ds[i].setFont(FONT_NORMAL);
				g2ds[i].drawString(RESTART_MSG,
					centerX(FONT_NORMAL, RESTART_MSG, g2ds[i]), display.h(0)/2-5);
			}
		}
		shipStatus(g2ds[0], ship1);
		shipStatus(g2ds[1], ship2);
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
		scenario.setAIFrequency(10);
		scenario.setSpeedRatio(.25f);
		scenario.setScalingRatio(.25f);
		scenario.setDensity(.25f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(ship.getColor());
		g2d.drawString("Armor: " + hull,
			display.w(-110), display.h(-39));
		g2d.setColor(COLOR);
		g2d.drawString("Deaths: " + ship.deaths,
			display.w(-110), display.h(-19));
	}
}
