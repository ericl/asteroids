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
		if (scenario.done()) {
			g2d.setColor(Color.GRAY);
			g2d.setFont(NORMAL);
			g2d.drawString(RESTART, centerX(NORMAL, RESTART, g2d),display.h(0)/2+19);
			g2d.setColor(Color.ORANGE);
			g2d.setFont(CENTER);
			String score = "Score: " + scenario.score();
			g2d.drawString(score, centerX(CENTER, score, g2d), display.h(0)/2);
			if (!(scenario instanceof Field)) return;
			g2d.drawString("High Scores", centerX(CENTER, "High Scores", g2d), display.h(0)/2+50);
			g2d.setFont(NORMAL);
			g2d.setColor(Color.GRAY);
			stats.build(((Field)scenario).id, "TestName", scenario.score());
			for (int i=0; i<5; i++)
				g2d.drawString(stats.get(i+1), centerX(CENTER, stats.get(i+1), g2d), display.h(0)/2+69+19*i);
		}
		shipStatus(g2d);
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
		stats.reset();		
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, ship, id);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setColor(Color.gray);
		g2d.setFont(NORMAL);
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",
				display.w(-110),display.h(-35));
			g2d.drawString("Asteroids: " +
				scenario.score(),display.w(-110),display.h(-15));
		} else {
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",
				display.w(-110),display.h(-95));
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
