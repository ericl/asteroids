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

import net.phys2d.raw.World;

import static asteroids.AbstractGame.Level.*;

public class MPAsteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500, BASE_HEIGHT = 500;
	protected Field scenario;
	protected Radar[] pointer;
	protected int[] deaths;
	protected Entity[] ships;
	protected DynamicEntity[] swap;
	protected StarField k;
	protected boolean restart;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids(2);
		game.mainLoop();
	}

	protected Display makeDisplay() {
		frame.setLocationByPlatform(true);
		GridLayout layout = new GridLayout((int)Math.sqrt(NUM_PLAYERS), (int)Math.sqrt(NUM_PLAYERS) + 1, 2, 2);
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

	public MPAsteroids(int num) {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT), num);
		pointer = new Radar[NUM_PLAYERS];
		deaths = new int[NUM_PLAYERS];
		ships = new Entity[NUM_PLAYERS];
		swap = new DynamicEntity[NUM_PLAYERS];
		for (int i=2; i < NUM_PLAYERS; i++) {
			swap[i] = new DynamicEntity(randomEntity(world));
			ships[i] = swap[i].newProxyInstance();
		}
		if (NUM_PLAYERS > 1) {
			swap[1] = new DynamicEntity(randomEntity(world));
			ships[1] = swap[1].newProxyInstance();
			HumanShipAI human = new HumanShipAI(world, ships[1], 500, true, null) {
				public void keyPressed(KeyEvent e) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_Q:
							return;
					}
					super.keyPressed(e);
				}

				public String toString() {
					return "Controls: arrow keys, space, f, c";
				}
			};
			swap[1].setAI(human);
			frame.addKeyListener(human);
		}

		swap[0] = new DynamicEntity(randomEntity(world));
		ships[0] = swap[0].newProxyInstance();

		HumanShipAI human = new HumanShipAI(world, ships[0], 500, true, null) {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': ship.modifyTorque(-8e-5f); notifyInput(true); break;
					case 'd': ship.modifyTorque(8e-5f); notifyInput(true); break;
					case 'w': ship.setAccel(7.5f); notifyInput(true); break;
					case 's': ship.setAccel(-3.75f); notifyInput(true); break;
					case '`': ship.startFiring(); notifyInput(true); break;
					case '1': ship.startLaunching(); notifyInput(true); break;
					case '2':
						if (ship.isVisible())
							ship.cloak();
						else
							ship.uncloak();
						notifyInput(false); break;
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

			public String toString() {
				return "Controls: wasd, `, 1, 2";
			}
		};
		swap[0].setAI(human);
		frame.addKeyListener(human);
		for (int i=0; i < NUM_PLAYERS; i++)
			pointer[i] = new Radar(ships[i], display, world);
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
		for (int i=0; i < NUM_PLAYERS; i++) {
			pointer[i].drawTo(g2ds[i]);
			if (!ships[i].isVisible())
				display.drawBody(ships[i], i);
		}
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
			shipStatus(g2ds[i], ships[i], deaths[i] + (ships[i].dead() ? 1 : 0));
		}
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r':
			for (int i=0; i < NUM_PLAYERS; i++)
				if (ships[i].canExplode())
					deaths[i]++;
			restart = true; break;
		}
	}

	public static Entity randomEntity(World world) {
		switch ((int)(6*Math.random())) {
			case 1:
				return new Jug(world);
			case 2:
				return new Frigate(world, true);
			case 3:
				return new Terror(world);
			case 4:
				return new Swarm(world);
			default:
				return new Ship(world, false);
		}
	}

	public void newGame() {
		super.newGame();
		for (int i=2; i < NUM_PLAYERS; i++) {
			final int foo = i;
			swap[i].setEntity(randomEntity(world));
			ships[i].setPosition(350*foo, 200*(foo % 2));
		}
		if (NUM_PLAYERS > 1) {
			swap[1].setEntity(randomEntity(world));
			ships[1].setPosition(400, 200);
		}
		swap[0].setEntity(randomEntity(world));
		k.init();
		AbstractGame.globalLevel = BLUE;
		scenario = new Field(world, display, "fight", ships);
		scenario.setAIFrequency(0);
		scenario.setSpeedRatio(.25f);
		scenario.setScalingRatio(.25f);
		scenario.setDensity(.80f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Entity ship, int deaths) {
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
		g2d.drawString("Deaths: " + deaths,
			display.w(-110), display.h(-39));
		g2d.drawString("Cloak: " + (int)(ship.cloakTime()/1000) + "s", display.w(-110), display.h(-19));
		g2d.drawString(ship.toString(), 10, 20);
		int pos = 20;
		if (ship.numMissiles() > 0) {
			g2d.drawString("Missiles: " + ship.numMissiles(), display.w(-90), pos);
			pos += 20;
		}
		if (ship.numBeams() > 0)
			g2d.drawString("Power: " + ship.numBeams(), display.w(-90), pos);
	}
}
