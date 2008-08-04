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

import javax.swing.JSplitPane;

import asteroids.bodies.*;

import asteroids.display.Display2;

import asteroids.handlers.*;

import net.phys2d.math.Vector2f;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

/**
 * Two-player, unscored game where the players compete against each other.
 */
public class MPAsteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500, BASE_HEIGHT = 500;
	protected Field scenario;
	protected JSplitPane jsplit;
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
		// redirect canvas keyevents to the frame
		final KeyboardFocusManager manager =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getSource() == frame)
					return false;
				manager.redispatchEvent(frame, e);
				return true;
			}
		});
		Canvas a, b;
		jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		         a = new Canvas(), b = new Canvas());
		a.setSize(dim);
		b.setSize(dim);
		a.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		b.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		jsplit.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		frame.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		jsplit.setDividerLocation(.5);
		jsplit.setVisible(true);
		frame.setContentPane(jsplit);
		return new Display2(frame, dim, a, b);
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		dim.setSize(BASE_WIDTH*2, BASE_HEIGHT); // workaround
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				jsplit.setDividerLocation(.5);
			}
		});
		frame.addKeyListener(ship2 = new Ship(world));
		frame.addKeyListener(ship1 = new Ship(world) {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': torque = -.00008f; break;
					case 'd': torque = .00008f; break;
					case 'w': accel = 10; break;
					case 's': accel = -5; break;
					case '`': fire = true; break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a':
					case 'd': torque = 0; break;
					case 'w':
					case 's': accel = 0; break;
					case '`': fire = false; break;
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
		pLeft = new Pointer(ship1, ship2, display);
		pRight = new Pointer(ship2, ship1, display);
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
		Vector2f scaler = v(dim.getWidth()*.25, dim.getHeight()*.5);
		display.setCenter(sub(ship1.getPosition(), scaler),
		                  sub(ship2.getPosition(), scaler));
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
					centerX2(FONT_NORMAL, RESTART_MSG, g2ds[i]), display.h(0)/2-5);
			}
		}
		shipStatus(g2ds[0], ship1);
		shipStatus(g2ds[1], ship2);
	}

	protected int centerX2(Font f, String s, Graphics2D g2d) {
		return (int)((dim.getWidth()/2 - g2d.getFontMetrics(f)
				.getStringBounds(s, g2d).getWidth())/2);
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': restart = true; break;
		}
	}

	public void newGame() {
		k.init();
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, id, ships);
		scenario.setInitialSpeed(10);
		scenario.setScalingConstant(.5f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(ship.statusColor());
		g2d.drawString("Armor: " + hull,
			display.w(0)/2-110,display.h(-59));
		g2d.setColor(COLOR);
		g2d.drawString("Deaths: " + ship.deaths,
			display.w(0)/2-110,display.h(-39));
	}
}
