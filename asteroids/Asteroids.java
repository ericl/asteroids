package asteroids;
import java.awt.*;
import java.awt.event.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class Asteroids extends AbstractGame {
	private static final int BASE_WIDTH = 500;
	private static final int BASE_HEIGHT = 500;
	protected final Ship ship;
	protected Field scenario;
	protected boolean restart;
	protected FiniteStarField k;
	protected boolean scoresBuilt;
	protected Thread scoreBuilder;
	protected String name = System.getProperty("user.name");
	
	private class ScoreBuilder extends Thread {
		public void run() {
			stats.build(name, scenario);
			scoresBuilt = true;
		}
	}

	public void enterPause(boolean pause) {
		if (pause)
			ship.pause();
		else
			ship.unpause();
	}

	public static void main(String[] args) {
		AbstractGame game = new Asteroids();
		game.mainLoop();
	}

	public Asteroids() {
		super("Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		frame.addKeyListener(ship = new Ship(world, stats));
		display.setBackground("pixmaps/background2.jpg");
		k = new FiniteStarField(display);
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
			g2d.drawString(RESTART_MSG, display.w(-115),display.h(-13));
			g2d.setColor(COLOR_BOLD);
			g2d.setFont(FONT_BOLD);
			String score = "Your Score: " + scenario.score();
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
		}
	}

	public void newGame() {
		k.init();
		scoreBuilder = new ScoreBuilder();
		scoresBuilt = false;
		stats.reset();		
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, ship, id);
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
			scenario.score(),display.w(-110),display.h(-15));
	}
}
