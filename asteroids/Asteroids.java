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
import javax.swing.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;

/**
 * Single-player asteroid game.
 */
public class Asteroids extends AbstractGame {
	private Ship ship;
	private Field scenario;
	private StarField k;
	private Thread scoreBuilder;
	private String name = System.getProperty("user.name");
	private boolean restart, scoresBuilt;
	private static final int BASE_WIDTH = 700, BASE_HEIGHT = 700;
	
	protected class ScoreBuilder extends Thread {
		public void run() {
			stats.build(name);
			scoresBuilt = true;
		}
	}

	public static void main(String[] args) {
		AbstractGame game = new Asteroids();
		game.mainLoop();
	}

	public Asteroids() {
		super("Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		frame.addKeyListener(ship = new Ship(world));
		ship.addStatsListener(stats);
		Ship.setMax(2);
		Ship.setSpeed(.75f);
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
		display.setCenter(sub(ship.getPosition(), scale(v(dim), .5f)));
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics()[0];
		g2d.setColor(COLOR);
		g2d.setFont(FONT_BOLD);
		g2d.drawString("\"" + scenario.toString() + "\"", 10, 40);
		if (scenario.done()) {
			g2d.setColor(COLOR);
			g2d.setFont(FONT_NORMAL);
			g2d.drawString("N - Change Name", display.w(-115),display.h(-30));
			g2d.drawString(RESTART_MSG, display.w(-115),display.h(-13));
			g2d.setColor(COLOR_BOLD);
			g2d.setFont(FONT_BOLD);
			String score = name + "'s Score: " + stats.score();
			g2d.drawString(score, centerX(FONT_BOLD, score, g2d), display.h(0)/2-20);
			if (!scoreBuilder.isAlive() && !scoresBuilt)
				scoreBuilder.start();
			else if (scenario instanceof Field)
				drawHighScores(g2d);
			else
				System.err.println("Can't get to high scores.");
		} else {
			shipStatus(g2d);
		}
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
			case 'q': System.exit(0); break;
		}
	}

	public void newGame() {
		k.init();
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, id, ship);
		stats.reset(scenario);		
		scenario.setDensity(.5f);
		scenario.setScalingConstant(1f);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(ship.statusColor());
		g2d.drawString("Armor: " + hull,
			display.w(-110),display.h(-35));
		g2d.setColor(COLOR);
		g2d.drawString("Asteroids: " +
			scenario.asteroids(),display.w(-110),display.h(-15));
	}

	public void changeName() {
		String s = (String)JOptionPane.showInputDialog(
			new JFrame(),
			"Who are you?",
			"Asteroids",
			JOptionPane.PLAIN_MESSAGE,
			null, null, name);
		if (s != null && !s.isEmpty())
			stats.edit(name = s);
	}
}
