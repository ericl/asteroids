import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.strategies.*;
import asteroids.display.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import java.util.*;
import static asteroids.Util.*;

public class Asteroids extends AbstractGame 
{
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	protected final Ship ship;
	protected Scenario scenario;
	protected int verbosity = 0;
	protected int border = 300, buf = 500;
	protected LinkedList<Vector2f> stars = new LinkedList<Vector2f>();
	protected StarField k;
	
	public static void main(String[] args) {
		AbstractGame game = new Asteroids();
		game.mainLoop();
	}

	public Asteroids() {
		super("Asteroids", WIDTH, HEIGHT);
		ship = new Ship(world);
		frame.addKeyListener(ship);
		scenario = new Scenario(world, ship, "basic");
		k = new StarField(ship, display);
		scenario.start();
	}

	protected void update() {
		scenario.update();
		display.setCenter(ship.getPosition());
	}

	protected void postWorld() {
		Graphics2D g2d = display.getGraphics();
		if (scenario.done()) {
			g2d.setColor(Color.gray);
			g2d.drawString("Score: " + scenario.score(), WIDTH/2-27, HEIGHT/2+5);
		}
		shipStatus(g2d);
	}
	
	protected void preWorld()
	{
		k.starField();
	}
	
	protected void keyHit(KeyEvent event) {
		switch (event.getKeyChar()) {
			case 'r': scenario.start(); break;
			case 'm': verbosity++; break;
		}
	}

	private void shipStatus(Graphics2D g2d) {
		g2d.setColor(Color.gray);
		if (verbosity % 2 == 0) {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-95,HEIGHT-35);
			g2d.drawString("Speed: " +
			(int)(10*ship.getVelocity().length())/10f,WIDTH-95,HEIGHT-15);
		} else {
			g2d.drawString("Armor: " +
			(int)(ship.getDamage()*1000)/10+"%",WIDTH-110,HEIGHT-95);
			g2d.drawString("Speed: " +
			(int)(1000*ship.getVelocity().length())/1000f,WIDTH-110,HEIGHT-75);
			g2d.drawString("Xcoord: " +
			(int)(ship.getPosition().getX() - WIDTH/2),WIDTH-110,HEIGHT-55);
			g2d.drawString("Ycoord: " +
			(int)(-ship.getPosition().getY() + HEIGHT/2),WIDTH-110,HEIGHT-35);
			g2d.drawString("Asteroids: " + scenario.score(),WIDTH-110,HEIGHT-15);
		}
	}
}
