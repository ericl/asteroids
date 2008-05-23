package asteroids;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.strategies.*;
import asteroids.display.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class MPAsteroids extends AbstractGame {
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	protected final Ship ship1, ship2;
	protected Ship[] ships = new Ship[2];
	protected Scenario scenario;
	protected int verbosity = 0;

	public static void main(String[] args) {
		AbstractGame game = new MPAsteroids();
		game.mainLoop();
	}

	public MPAsteroids() {
		super("Multiplayer Asteroids", WIDTH, HEIGHT);
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

	protected void postWorld(Display display) {
		Graphics2D[] g2ds = display.getGraphics();
		for (Graphics2D g2d : g2ds)
			if (scenario.done()) {
				g2d.setColor(Color.black);
				g2d.drawString("Score: " + scenario.score(),
				WIDTH/2-27, HEIGHT/2+5);
			}
		shipStatus(g2ds[0], ship1);
		shipStatus(g2ds[1], ship2);
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
			scenario = new ShipBattle(world, v(WIDTH,HEIGHT), ships, id);
			scenario.start();
		}
	}

	private void shipStatus(Graphics2D g2d, Ship ship) {
		g2d.setColor(Color.gray);
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-110,HEIGHT-59);
			g2d.drawString("MPAsteroids: " + scenario.score(),WIDTH-110,HEIGHT-39);
		} else {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-110,HEIGHT-119);
			g2d.drawString("Speed: " +
			(int)(1000*ship.getVelocity().length())/1000f,WIDTH-110,HEIGHT-99);
			g2d.drawString("Xcoord: " +
			(int)(ship.getPosition().getX()),WIDTH-110,HEIGHT-79);
			g2d.drawString("Ycoord: " +
			(int)(-ship.getPosition().getY()),WIDTH-110,HEIGHT-59);
			g2d.drawString("MPAsteroids: " + scenario.score(),WIDTH-110,HEIGHT-39);
		}
	}
}
