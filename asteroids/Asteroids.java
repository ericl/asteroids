package asteroids;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class Asteroids extends AbstractGame {
	private Ship ship;
	private Field scenario;
	private StarField k;
	private Thread scoreBuilder;
	private String name = System.getProperty("user.name");
	private boolean restart;
	private boolean scoresBuilt;
	private static final int BASE_WIDTH = 700;
	private static final int BASE_HEIGHT = 700;
	
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
		display.setCenter(ship.getPosition());
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics();
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
		scenario = new Field(world, display, ship, id);
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
