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
	protected boolean restart;
	protected FiniteStarField k;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids();
		game.mainLoop();
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", new Dimension(BASE_WIDTH, BASE_HEIGHT));
		frame.addKeyListener(ship2 = new Ship(world, stats));
		frame.addKeyListener(ship1 = new Ship2(world, stats));
		ships[0] = ship1;
		ships[1] = ship2;
		Ship.setMax(2);
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
		display.setCenter(ship1.getPosition(), ship2.getPosition());
	}

	protected void preWorld() {
		k.starField();
	}

	protected void postWorld() {
		Graphics2D[] g2ds = display.getAllGraphics();
		for (int i=0; i < g2ds.length; i++) {
			if (ships[i].canExplode()) {
				g2ds[i].setColor(COLOR);
				g2ds[i].setFont(FONT_NORMAL);
				g2ds[i].drawString(RESTART_MSG,
					centerX(FONT_NORMAL, RESTART_MSG, g2ds[i]), display.h(0)/2-5);
			}
		}
		shipStatus(g2ds[0], ship1);
		shipStatus(g2ds[1], ship2);
	}

	public void keyTyped(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': restart = true; break;
			case 'm': verbosity++; break;
		}
	}

	public void newGame() {
		k.init();
		int id = Field.ids[(int)range(0,Field.ids.length)];
		scenario = new Field(world, display, ships, id);
		scenario.start();
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setFont(FONT_NORMAL);
		String hull = "Infinity";
		if (ship.getDamage() != Double.POSITIVE_INFINITY)
			hull = (int)(ship.getDamage()*1000)/10+"%";
		g2d.setColor(shipColor(ship));
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " + hull,
				display.w(-110),display.h(-59));
			g2d.setColor(COLOR);
			g2d.drawString("Deaths: " + ship.deaths,
				display.w(-110),display.h(-39));
		} else {
			g2d.drawString("Armor: " + hull,
				display.w(-110),display.h(-119));
			g2d.setColor(COLOR);
			g2d.drawString("Speed: " +
				(int)(1000*ship.getVelocity().length())/1000f,
				display.w(-110),display.h(-99));
			g2d.drawString("Xcoord: " +
				(int)(ship.getPosition().getX()),
				display.w(-110),display.h(-79));
			g2d.drawString("Ycoord: " +
				(int)(-ship.getPosition().getY()),
				display.w(-110),display.h(-59));
			g2d.drawString("Deaths: " + ship.deaths,
				display.w(-110),display.h(-39));
		}
	}
}
