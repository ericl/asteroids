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
	protected Scenario scenario;
	protected boolean restart;
	protected int verbosity = 0;
	protected FiniteStarField k;
	protected boolean scoresBuilt;
	protected Thread scoreBuilder;
	protected String name = System.getProperty("user.name");
	
	private class ScoreBuilder extends Thread {
		public void run() {
			stats.build(((Field)scenario).id, name, scenario.score());
			scoresBuilt = true;
		}
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
					display.h(0)/2+19*i);
	}

	protected void preWorld() {
		k.starField();
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': restart = true; break;
			case 'm': verbosity++; break;
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
		if (verbosity % 2 == 0) {
			g2d.setColor(shipColor(ship));
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",
				display.w(-110),display.h(-35));
			g2d.setColor(COLOR);
			g2d.drawString("Asteroids: " +
				scenario.score(),display.w(-110),display.h(-15));
		} else {
			g2d.setColor(shipColor(ship));
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",
				display.w(-110),display.h(-95));
			g2d.setColor(COLOR);
			g2d.drawString("Speed: " +
				(int)(1000*ship.getVelocity().length())/1000f,
				display.w(-110),display.h(-75));
			g2d.drawString("Xcoord: " +
				(int)(ship.getPosition().getX()),
				display.w(-110),display.h(-55));
			g2d.drawString("Ycoord: " +
				(int)(-ship.getPosition().getY()),
				display.w(-110),display.h(-35));
			g2d.drawString("Asteroids: " + scenario.score(),
				display.w(-110),display.h(-15));
		}
	}
}
