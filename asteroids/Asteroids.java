/**
* Single-player asteroid game.
*/

package asteroids;

import java.awt.*;

import java.awt.event.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.*;

import asteroids.ai.*;

import asteroids.bodies.*;

import asteroids.handlers.*;

import net.phys2d.math.ROVector2f;

import static asteroids.AbstractGame.Level.*;

import static asteroids.MPAsteroids.*;

import static asteroids.Util.*;

public class Asteroids extends AbstractGame {
	private static File nameFile = mktemp(".asteroids-name");
	private Entity ship;
	private long spaceTime;
	private Radar radar;
	private Field scenario;
	private StarField k;
	private Thread scoreBuilder;
	private String name = System.getProperty("user.name");
	private DynamicEntity swapper;
	private HumanShipAI human;
	private boolean restart, scoresBuilt, multi;
	private static final int BASE_WIDTH = 700, BASE_HEIGHT = 700;

	private boolean isSpecial(String name) {
		if (name.indexOf("juggernaut") >= 0)
			return true;
		else if (name.indexOf("frigate") >= 0)
			return true;
		else if (name.indexOf("blue terror") >= 0)
			return true;
		else if (name.indexOf("heavy rock") >= 0)
			return true;
		return false;
	}
	
	protected class ScoreBuilder extends Thread {
		public void run() {
			if (!devmode) {
				if (!isSpecial(name) && !isSpecial(scenario.startingName()))
					stats.build(name);
				else
					stats.build(scenario.startingName());
			}
			scoresBuilt = true;
		}
	}

	public void newGame() {
		super.newGame();
		k.init();
		AbstractGame.globalLevel = START;
		if (name.indexOf("juggernaut") >= 0)
			swapper.setEntity(new Jug(world));
		else if (name.indexOf("frigate") >= 0)
			swapper.setEntity(new Frigate(world));
		else if (name.indexOf("blue terror") >= 0)
			swapper.setEntity(new Terror(world));
		else if (name.indexOf("heavy rock") >= 0)
			swapper.setEntity(new Swarm(world));
		else
			swapper.setEntity(new Ship(world));
		devmode = false;
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		scenario = new Field(world, display, name, ship);
		scenario.setAIFrequency(.01);
		stats.reset(scenario);
		stats.setShip(ship);
		scenario.setDensity(.4f);
		scenario.setScalingRatio(.33f);
		scenario.start();
	}

	public static void main(String[] args) {
		try {
			new Asteroids().mainLoop();
		} catch (Throwable e) {
			oops(e);
		}
	}

	public Asteroids() {
		super("Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT), 1);
		try {
			FileInputStream stream = new FileInputStream(nameFile);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			name = new String(bytes).trim();
		} catch (Exception e) {
			System.err.println("on reading name: " + e);
		} finally {
			if (name == null || name.length() < 1)
				name = System.getProperty("user.name");
		}
		swapper = new DynamicEntity(new Ship(world));
		ship = swapper.newProxyInstance();
		radar = new Radar(ship, display, world);
		human = new HumanShipAI(world, ship, Integer.MAX_VALUE, true, display.getDimension());
		swapper.setAI(human);
		frame.addKeyListener(human);
		display.addMouseInputListener(human);
		display.setBackground("pixmaps/background2.jpg");
		k = new StarField(display);
		newWelcome();
	}

	protected void update() {
		if (restart) {
			restart = false;
			newGame();
		}
		if (multi) {
			Chooser.main(new String[0]);
			frame.dispose();
			running = false;
		}
		scenario.update();
		display.setCenter(scenario.getCenter());
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics()[0];
		g2d.setColor(COLOR);
		g2d.setFont(FONT_BOLD);
		int vpos = 20;
		if (AbstractGame.globalLevel == DONE)
			g2d.setColor(COLOR_BOLD);
		for (String string : scenario.toString().split("\n")) {
			g2d.drawString(string, 10, vpos);
			vpos += 20;
		}
		if (scenario.done()) {
			stats.freezeScores();
			g2d.setFont(FONT_NORMAL);
			g2d.setColor(COLOR);
			g2d.drawString("N - Change Name", display.w(-115),display.h(-30));
			g2d.drawString("SPACE - Retry", display.w(-115),display.h(-13));
			String sname = name;
			if ((isSpecial(name) || isSpecial(scenario.startingName())) && !name.equals(scenario.startingName()))
				sname = "(" + name + ")";
			g2d.setColor(COLOR_BOLD);
			String s = "s";
			if (name.length() > 0 && Character.isUpperCase(name.charAt(0)))
				s = "S";
			String score = sname + "'s " + s + "core: " + stats.score();
			if (devmode)
				score = "Score: " + stats.score();
			renderCenter(g2d, FONT_BOLD, score, 40);
			renderCenter(g2d, FONT_NORMAL, ship.killer(), 25);
			if (!scoreBuilder.isAlive() && !scoresBuilt)
				scoreBuilder.start();
			else if (scenario instanceof Field)
				drawHighScores(g2d);
			else
				System.err.println("Can't get to high scores.");
		} else if (scenario instanceof WelcomeScreen) {
			g2d.setColor(COLOR_BOLD);
			renderCenter(g2d, FONT_VERY_BOLD, "Asteroids", 80);
			renderCenter(g2d, FONT_NORMAL, "This package is provided under the terms of the BSD License*", 55);
			renderCenter(g2d, FONT_NORMAL, "Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster.", 35);
			g2d.setColor(COLOR);
			renderCenter(g2d, FONT_NORMAL, "Use arrow keys to navigate; space/mouse to fire.", 11);
			renderCenter(g2d, FONT_NORMAL, "Q - cancel game", -10);
			renderCenter(g2d, FONT_NORMAL, "C - cloak ship", -30);
			renderCenter(g2d, FONT_NORMAL, "F - launch missile", -50);
			renderCenter(g2d, FONT_BOLD, "Press SPACE to continue.", -90);
			g2d.setFont(FONT_NORMAL);
			g2d.drawString("M - multiplayer mode", display.w(-140),display.h(-10));
			g2d.drawString("* see README inside jar file", 10, display.h(-10));
		} else {
			shipStatus(g2d);
		}
	}

	private void renderCenter(Graphics2D g2d, Font font, String string, float ydelta) {
		g2d.setFont(font);
		g2d.drawString(string, centerX(font, string, g2d), display.h(0)/2-ydelta);
	}

	public void drawHighScores(Graphics2D g2d) {
		g2d.setColor(COLOR);
		String loading = "Loading high scores...";
		if (scoreBuilder.isAlive())
			g2d.drawString(loading, centerX(FONT_NORMAL,loading,g2d),
				display.h(0)/2);
		else
			for (int i=0; i<5; i++) {
				g2d.setFont(FONT_NORMAL);
				g2d.drawString(stats.get(i+1),
					centerX(FONT_NORMAL, stats.get(i+1), g2d),
					display.h(0)/2+33*i);
				g2d.setFont(FONT_SMALL);
				g2d.drawString(stats.getCause(i+1),
					centerX(FONT_SMALL, stats.getCause(i+1), g2d),
					display.h(0)/2+12+33*i);
			}
	}

	protected void preWorld() {
		k.starField();
		radar.drawTo(display.getGraphics()[0]);
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'n': changeName(); break;
			case 'm':
				if (scenario != null && scenario instanceof WelcomeScreen)
					multi = true;
				break;
			case ' ':
				long current = System.currentTimeMillis();
				long diff = current - spaceTime;
				spaceTime = current;
				if (diff > 500 && scenario != null && (scenario instanceof WelcomeScreen || scenario.done()))
					restart = true;
				break;
		}
		if (devmode) switch (event.getKeyChar()) {
			case '?':
				System.out.println("Bodies: " + world.getBodies().size());
				System.out.println("Position: " + ship.getPosition());
				System.out.println("Velocity: " + ship.getVelocity());
				System.out.println("Rotation: " + ship.getRotation());
				break;
			case '|':
				ship.gainBeams(1000);
				break;
			case 'M':
				ship.addMissiles(10);
				break;
			case 'H':
				ship.setHealth(1);
				break;
			case 'S':
				ship.raiseShields();
				break;
			case 'I':
				ship.gainInvincibility(20000, 4000);
				break;
			case '!':
				ship.gainInvincibility(Integer.MAX_VALUE, 0);
				break;
			case 'W':
				ship.upgradeWeapons();
				break;
			case 'T':
				ROVector2f pos = ship.getPosition(), vel = ship.getVelocity();
				float rot = ship.getRotation();
				swapper.setEntity(randomEntity(world));
				ship.setPosition(pos.getX(), pos.getY());
				ship.adjustVelocity(vel);
				ship.setRotation(rot);
				break;
			case '1':
				AbstractGame.globalLevel = START;
				break;
			case '2':
				AbstractGame.globalLevel = EASY;
				break;
			case '3':
				AbstractGame.globalLevel = MEDIUM;
				break;
			case '4':
				AbstractGame.globalLevel = HARD;
				break;
			case '5':
				AbstractGame.globalLevel = BLUE;
				break;
			case '6':
				AbstractGame.globalLevel = SWARM;
				break;
			case '7':
				AbstractGame.globalLevel = DONE;
				break;
		}
	}

	public void newWelcome() {
		k.init();
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		scenario = new WelcomeScreen(world, display);
		stats.reset(scenario);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.health()*1000)/10 + "%";
		String shield = "";
		double s = ship.shieldInfo();
		if (s >= 0)
			shield = "Shield: " + (int)(s*1000)/10 + "%";
		g2d.setColor(COLOR);
		g2d.drawString(shield, display.w(-90),display.h(-55));
		g2d.setColor(ship.getColor());
		g2d.drawString("Hull: " + hull, display.w(-90),display.h(-35));
		g2d.setColor(COLOR);
		g2d.drawString("Cloak: " + (int)(ship.cloakTime()/1000) + "s", display.w(-90), display.h(-15));
		g2d.drawString("Score: " + stats.score(), 15, display.h(-15));
		int pos = 20;
		if (ship.numMissiles() > 0) {
			g2d.drawString("Missiles: " + ship.numMissiles(), display.w(-90), pos);
			pos += 20;
		}
		if (ship.numBeams() > 0)
			g2d.drawString("Power: " + ship.numBeams(), display.w(-90), pos);
	}

	public void changeName() {
		pause();
		String s = (String)JOptionPane.showInputDialog(
			new JFrame(),
			"Who are you?",
			"Asteroids",
			JOptionPane.PLAIN_MESSAGE,
			null, null, name);
		if (s != null) {
			s = s.trim();
			if (s.equals("dev"))
				devmode = true;
			else if (!s.equals("")) {
				try {
					File swp = new File(nameFile.getPath() + ".swp");
					FileOutputStream stream = new FileOutputStream(swp);
					stream.write(s.getBytes());
					swp.renameTo(nameFile);
				} catch (Exception e) {
					System.err.println(e);
				}
				name = s;
				if (!isSpecial(s) && !isSpecial(scenario.startingName()))
					if (!devmode)
						stats.edit(s);
			}
		}
		unpause();
	}
}
