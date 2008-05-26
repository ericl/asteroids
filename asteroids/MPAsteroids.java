package asteroids;
import java.awt.*;
import java.awt.event.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class MPAsteroids extends MPGame {
	private static final int BASE_WIDTH = 500;
	private static final int BASE_HEIGHT = 500;
	protected final Ship ship1, ship2;
	protected Ship[] ships = new Ship[2];
	protected Scenario scenario;
	protected int verbosity = 0;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids();
		game.mainLoop();
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		display.setBackground("pixmaps/opo9929b.jpg");
		frame.addKeyListener(ship2 = new Ship(world));
		frame.addKeyListener(ship1 = new Ship2(world));
		ships[0] = ship1;
		ships[1] = ship2;
		newGame();
	}

	protected void update() {
		scenario.update();
		display.setCenter(ship1.getPosition(), ship2.getPosition());
	}

	protected void postWorld() {
		Graphics2D[] g2ds = display.getAllGraphics();
		for (Graphics2D g2d : g2ds) {
			if (scenario.done()) {
				g2d.setColor(Color.black);
				g2d.drawString("Score: " + scenario.score(),
				display.w(0)/2-27, display.h(0)/2+5);
			}
		}
		shipStatus(g2ds[0], ship1);
		shipStatus(g2ds[1], ship2);
	}

	protected void pause() {
		if (!pause) {
			pause = true;
			synchronized (display) {
				for (Graphics2D g2d : display.getAllGraphics()) {
					g2d.setColor(new Color(100,100,100,100));
					g2d.fillRect(0,0,display.w(0),display.h(0));
					g2d.setFont(new Font("SanSerif", Font.BOLD, 15));
					g2d.setColor(Color.RED);
					g2d.drawString("PAUSED",20,display.h(-20));
					display.show();
				}
			}
		}
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': newGame(); break;
			case 'm': verbosity++; break;
		}
	}

	public void newGame() {
		String id = ShipBattle.ids[(int)range(0,Field.ids.length)];
		// switching scenarios would give inconsistent output
		// (e.g. zero score for an instant)
		synchronized (world) {
			scenario = new ShipBattle(world, display, ships, id);
			scenario.start();
		}
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setColor(Color.gray);
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",
				display.w(-110),display.h(-59));
			g2d.drawString("Deaths: " + ship.deaths,
				display.w(-110),display.h(-39));
		} else {
			g2d.drawString("Armor: " +
				(int)(ship.getDamage()*1000)/10+"%",	
				display.w(-110),display.h(-99));
			g2d.drawString("Speed: " +
				(int)(1000*ship.getVelocity().length())/1000f,
				display.w(-110),display.h(-79));
			g2d.drawString("Xcoord: " +
				(int)(ship.getPosition().getX()),
				display.w(-110),display.h(-59));
			g2d.drawString("Ycoord: " +
				(int)(-ship.getPosition().getY()),
				display.w(-110),display.h(-39));
		}
	}
}
