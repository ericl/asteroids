/**
 * Two-player, unscored game where the players compete against each other.
 */

package asteroids;

import java.awt.*;

import java.awt.event.*;

import asteroids.ai.*;

import asteroids.bodies.*;

import asteroids.display.Display;

import asteroids.handlers.*;

import net.phys2d.math.ROVector2f;

import static asteroids.AbstractGame.Difficulty.*;

public class MPAsteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500, BASE_HEIGHT = 500;
	private static final int NUM_PLAYERS = 2;
	protected Field scenario;
	protected Pointer[] pointer = new Pointer[NUM_PLAYERS];
	protected Entity[] ships = new Entity[NUM_PLAYERS];
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
			Entity ship = new Frigate(world) {
				public void reset() {
					super.reset();
					setPosition(350*foo, 200*(foo % 2));
				}	
			};
			ships[i] = ship;
		}
		if (NUM_PLAYERS > 1) {
			Entity ship = new Terror(world) {
				public boolean canTarget() {
					return true;
				}

				public void reset() {
					super.reset();
					setPosition(400, 200);
				}

				public String toString() {
					return "Controls: arrow keys, space, f";
				}
			};
			HumanShipAI human = new HumanShipAI(world, ship, 500, true, null) {
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_Q:
							return;
					}
					super.keyPressed(e);
				}
			};
			frame.addKeyListener(human);
			ships[1] = ship;
		}

		Entity ship = new Frigate(world) {
			public boolean canTarget() {
				return true;
			}

			public String toString() {
				return "Controls: wasd, `, 1";
			}
		};

		HumanShipAI human = new HumanShipAI(world, ship, 500, true, null) {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': ship.modifyTorque(-8e-5f); notifyInput(true); break;
					case 'd': ship.modifyTorque(8e-5f); notifyInput(true); break;
					case 'w': ship.setAccel(7.5f); notifyInput(true); break;
					case 's': ship.setAccel(-3.75f); notifyInput(true); break;
					case '`': ship.startFiring(); notifyInput(true); break;
					case '1': ship.startLaunching(); notifyInput(true); break;
				}
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a':
					case 'd': ship.modifyTorque(0); notifyInput(false); break;
					case 'w':
					case 's': ship.setAccel(0); notifyInput(false); break;
					case '`': ship.stopFiring(); notifyInput(false); break;
					case '1': ship.stopLaunching(); notifyInput(false); break;
				}
			}
		};
		frame.addKeyListener(human);
		ships[0] = ship;
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
		AbstractGame.globalDifficulty = MEDIUM;
		scenario = new Field(world, display, ships);
		scenario.setAIFrequency(0);
		scenario.setSpeedRatio(.25f);
		scenario.setScalingRatio(.25f);
		scenario.setDensity(.40f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Entity ship) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.health()*1000)/10+"%";
		String shield = "";
		double s = ship.shieldInfo();
		if (s >= 0)
			shield = "Shield: " + (int)(s*1000)/10 + "%";
		g2d.setColor(COLOR);
		g2d.drawString(shield, display.w(-110),display.h(-79));
		g2d.setColor(ship.getColor());
		g2d.drawString("Hull: " + hull,
			display.w(-110), display.h(-59));
		g2d.setColor(COLOR);
		g2d.drawString("Missiles: " + ship.numMissiles(),
			display.w(-110), display.h(-39));
		g2d.drawString("Deaths: " + ship.numDeaths(),
			display.w(-110), display.h(-19));
		g2d.drawString(ship.toString(), 10, 20);
	}
}
