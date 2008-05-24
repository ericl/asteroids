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

public class Asteroids extends AbstractGame {
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	protected final Ship ship;
	protected Scenario scenario;
	protected int verbosity = 0;

	public static void main(String[] args) {
		AbstractGame game = new Asteroids();
		game.init();
		game.mainLoop();
	}

	public Asteroids() {
		super("Asteroids", WIDTH, HEIGHT);
		frame.addKeyListener(ship = new Ship(world));
		newGame();
	}

	protected void update() {
		scenario.update();
		getDisplay().setCenter(ship.getPosition());
	}

	protected void postWorld() {
		Graphics2D g2d = getDisplay().getGraphics();
		if (scenario.done()) {
			g2d.setColor(Color.gray);
			g2d.drawString("Score: " + scenario.score(),
			WIDTH/2-27, HEIGHT/2+5);
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
			scenario = new Field(world, v(WIDTH,HEIGHT), ship, id);
			scenario.start();
		}
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setColor(Color.gray);
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-110,HEIGHT-35);
			g2d.drawString("Asteroids: " + scenario.score(),WIDTH-110,HEIGHT-15);
		} else {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-110,HEIGHT-95);
			g2d.drawString("Speed: " +
			(int)(1000*ship.getVelocity().length())/1000f,WIDTH-110,HEIGHT-75);
			g2d.drawString("Xcoord: " +
			(int)(ship.getPosition().getX()),WIDTH-110,HEIGHT-55);
			g2d.drawString("Ycoord: " +
			(int)(-ship.getPosition().getY()),WIDTH-110,HEIGHT-35);
			g2d.drawString("Asteroids: " + scenario.score(),WIDTH-110,HEIGHT-15);
		}
	}
}
