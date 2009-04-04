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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.*;

import asteroids.bodies.*;

import asteroids.handlers.*;

import static asteroids.Util.*;

/**
 * Single-player asteroid game.
 */
public class Asteroids extends AbstractGame {
	private static File nameFile = new File(System.getProperty("user.home") + "/.asteroids-name");
	private Ship ship;
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
		frame.addKeyListener(ship = new Ship(world));
		ship.addStatsListener(stats);
		Ship.setMax(4);
		Ship.setSpeed(.25f);
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
			renderCenter(g2d, FONT_BOLD, score, 20);
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
			renderCenter(g2d, FONT_NORMAL, "Use arrow keys to navigate; space to fire.", 11);
			renderCenter(g2d, FONT_NORMAL, "X - cancel game", -10);
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
		g2d.setFont(FONT_NORMAL);
		g2d.setColor(COLOR);
		String loading = "Loading high scores...";
		if (scoreBuilder.isAlive())
			g2d.drawString(loading, centerX(FONT_NORMAL,loading,g2d),
				display.h(0)/2);
		else
			for (int i=0; i<5; i++)
				g2d.drawString(stats.get(i+1),
					centerX(FONT_NORMAL, stats.get(i+1), g2d),
					display.h(0)/2+5+19*i);
	}

	protected void preWorld() {
		k.starField();
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': restart = true; break;
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

	public void newGame() {
		k.init();
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, id, ship);
		scenario.setAIFrequency(.01);
		stats.reset(scenario);
		stats.setShip(ship);
		scenario.setDensity(.4f);
		scenario.setScalingRatio(.33f);
		scenario.start();
	}

	public void newWelcome() {
		k.init();
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new WelcomeScreen(world, display, id);
		stats.reset(scenario);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(ship.getColor());
		g2d.drawString("Armor: " + hull,
			display.w(-110),display.h(-55));
		g2d.setColor(COLOR);
		g2d.drawString("Score: " +
			stats.score(),display.w(-110),display.h(-35));
		g2d.drawString("Missiles: " + ship.numMissiles(),display.w(-110),display.h(-15));
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
