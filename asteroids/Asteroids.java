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

import static asteroids.AbstractGame.Level.*;

public class Asteroids extends AbstractGame {
	private static File nameFile = new File(System.getProperty("user.home") + "/.asteroids-name");
	private Entity ship;
	private Field scenario;
	private StarField k;
	private Thread scoreBuilder;
	private String name = System.getProperty("user.name");
	private boolean restart, scoresBuilt, multi;
	private static final int BASE_WIDTH = 700, BASE_HEIGHT = 700;
	
	protected class ScoreBuilder extends Thread {
		public void run() {
			stats.build(name);
			scoresBuilt = true;
		}
	}

	public void newGame() {
		k.init();
		AbstractGame.globalLevel = START;
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		scenario = new Field(world, display, ship);
		scenario.setAIFrequency(.01);
		stats.reset(scenario);
		stats.setShip(ship);
		scenario.setDensity(.4f);
		scenario.setScalingRatio(.33f);
		scenario.start();
	}

	public static void main(String[] args) {
		new Asteroids().mainLoop();
	}

	public Asteroids() {
		super("Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		try {
			FileInputStream stream = new FileInputStream(nameFile);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			name = new String(bytes);
		} catch (Exception e) {
			System.err.println(e);
		}
		ship = new Ship(world);
		HumanShipAI human = new HumanShipAI(world, ship, Integer.MAX_VALUE, true, display.getDimension());
		frame.addKeyListener(human);
		display.addMouseInputListener(human);
		ship.addStatsListener(stats);
		display.setBackground("pixmaps/background2.jpg");
		k = new StarField(display);
		newWelcome();
	}

	protected void update() {
		if (restart) {
			newGame();
			restart = false;
		}
		if (multi) {
			new MPAsteroids().mainLoop();
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
		for (String string : scenario.toString().split("\n")) {
			g2d.drawString(string, 10, vpos);
			vpos += 20;
		}
		if (scenario.done()) {
			stats.freezeScores();
			g2d.setColor(COLOR);
			g2d.setFont(FONT_NORMAL);
			g2d.drawString("N - Change Name", display.w(-115),display.h(-30));
			g2d.drawString(RESTART_MSG, display.w(-115),display.h(-13));
			g2d.setColor(COLOR_BOLD);
			String score = name + "'s Score: " + stats.score();
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
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': if (scenario.done()) restart = true; break;
			case 'n': changeName(); break;
			case 'm':
				if (scenario instanceof WelcomeScreen)
					multi = true;
				break;
			case ' ':
				if (scenario instanceof WelcomeScreen)
					restart = true;
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
		g2d.drawString(shield, display.w(-90),display.h(-75));
		g2d.setColor(ship.getColor());
		g2d.drawString("Hull: " + hull, display.w(-90),display.h(-55));
		g2d.setColor(COLOR);
		g2d.drawString("Cloak: " + (int)(ship.cloakTime()/1000) + "s", display.w(-90), display.h(-35));
		g2d.drawString("Missiles: " + ship.numMissiles(), display.w(-90), display.h(-15));
		g2d.drawString("Score: " + stats.score(), 15, display.h(-15));
	}

	public void changeName() {
		pause();
		String s = (String)JOptionPane.showInputDialog(
			new JFrame(),
			"Who are you?",
			"Asteroids",
			JOptionPane.PLAIN_MESSAGE,
			null, null, name);
		if (s != null && !s.equals("")) {
			try {
				File swp = new File(nameFile.getPath() + ".swp");
				FileOutputStream stream = new FileOutputStream(swp);
				stream.write(s.getBytes());
				swp.renameTo(nameFile);
			} catch (Exception e) {
				System.err.println(e);
			}
			stats.edit(name = s);
		}
		unpause();
	}
}
