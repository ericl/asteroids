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
	protected int verbosity = 0;

	public static void main(String[] args) {
		AbstractGame game = new Asteroids();
		game.mainLoop();
	}

	public Asteroids() {
		super("Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		frame.addKeyListener(ship = new Ship(world));
		newGame();
	}

	protected void update() {
		scenario.update();
		display.setCenter(ship.getPosition());
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics();
		if (scenario.done()) {
			g2d.setColor(Color.gray);
			g2d.drawString("Score: " +
				scenario.score(), display.w(0)/2-27, display.h(0)/2+5);
		}
		shipStatus(g2d);
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': newGame(); break;
			case 'm': verbosity++; break;
		}
	}

	public void newGame() {
		String id = Field.ids[(int)range(0,Field.ids.length)];
		// switching scenarios would give inconsistent output
		// (e.g. zero score for an instant)
		synchronized (world) {
			scenario = new Field(world, display, ship, id);
			scenario.start();
		}
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setColor(Color.gray);
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
