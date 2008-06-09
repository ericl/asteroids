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
import asteroids.display.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import asteroids.bodies.*;
import asteroids.handlers.*;

public class Protect extends AbstractGame {
	private Ship ship;
	private Defend scenario;
	private StarField k;
	private Pointer p;
	private Thread scoreBuilder;
	private String name = System.getProperty("user.name");
	private boolean restart;
	private boolean scoresBuilt;
	
	protected class ScoreBuilder extends Thread {
		public void run() {
			stats.build(name);
			scoresBuilt = true;
		}
	}

	public static void main(String[] args) {
		AbstractGame game = new Protect();
		game.mainLoop();
	}

	public Protect() {
		super("Protect Europa", Toolkit.getDefaultToolkit().getScreenSize());
		frame.addKeyListener(ship = new Ship(world));
		display.setBackground("pixmaps/background2.jpg");
		ship.addStatsListener(stats);
		Ship.setSpeed(.75f);
		Ship.setMax(20);
		k = new StarField(display);
		newGame();
	}

	protected void update() {
		if (restart) {
			newGame();
			restart = false;
		}
		display.setCenter(ship.getPosition());
		scenario.update();
	}

	protected Display makeDisplay() {
		frame.setUndecorated(true);
		frame.setResizable(false);
		GraphicsDevice myDevice =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
		try {
			myDevice.setFullScreenWindow(frame);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return new BasicDisplay(frame, dim);
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics();
		p.drawTo(g2d);
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
		g2d.setColor(COLOR);
		g2d.setFont(FONT_NORMAL);
		g2d.drawString("Q - Quit Game", 15, 25);
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
		scenario = new Defend(world, display, ship);
		stats.reset(scenario);		
		scenario.setInitialSpeed(20);
		scenario.setDensity(.1f);
		scenario.setScalingConstant(.5f);
		scenario.start();
		p = new Pointer(ship, scenario.getObject(), display);
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (!ship.isInvincible())
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(scenario.getObject().statusColor());
		g2d.drawString("Europa: " + scenario.getObject().getPercentDamage(),
			display.w(-110),display.h(-55));
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
