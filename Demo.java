import asteroids.display.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Demo {
	protected JFrame frame;
	protected Display d;
	protected boolean running;
	protected World world;
	protected float border = 300, buf = 500;
	protected int numrocks = 50, score;
	protected Ship ship;
	protected float xo, yo;
	protected int width, height;

	public static void main(String[] args) {
		Demo demo = new Demo();
	}

	public Demo() {
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		// trigger endFrame() events
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		world.addListener(new Exploder(world));
		frame = new JFrame("Asteroid Field Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		width = 500;
		height = 500;
		frame.setSize(width, height);
		running = true;
		
		int x = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getWidth()-width)/2;
		int y = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getHeight()-width)/2;
		
		frame.setLocation(x,y);

		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'r': init(); return;
				}
			}
		});
	
		d = new Display(frame);
		d.setBackground("opo9929b.jpg");
		init();
		mainLoop();
	}

	/*
	 * (Adaptive timing loop copied from AbstractDemo)
	 * Currently rendering takes 4x as long as a the 5 step physics update
	 * We want the physics to be asynchronous so that the game
	 * will play at the same speed no matter the frame rate.
	 * Obviously this is beyond the scope of this demo.
	 */
	protected void mainLoop() {
		long renderTime = 0, logicTime = 0;
		float dt = 0;
		Timer t = new Timer(60f);
		while (running) {
			long Xbegin = System.currentTimeMillis();
			long beforeRender = System.currentTimeMillis();
			d.drawWorld(world);
			drawGUI(dt, renderTime, logicTime);
			d.show();
			renderTime = System.currentTimeMillis() - beforeRender;
			ship.decrThrust();
			dt = t.tick();
			long beforeLogic = System.currentTimeMillis();
			for (int i=0;i<5;i++)
				world.step(dt);
			logicTime = System.currentTimeMillis() - beforeLogic;
			update();
		}
	}

	/**
	 * Resets game within demo.
	 */
	protected void init() {
		world.clear();
		xo = width/2;
		yo = height/2;
		d.setCenter(v(xo, yo));
		score = 0;
		world.setGravity(0,0);
		for (int i=0; i < numrocks; i++)
			world.add(newAsteroid());
		world.add(new Europa(150));
		ship = new Ship(world);
		frame.addKeyListener(ship);
		ship.setPosition((xo+width/2),(yo+height/2));
		world.add(ship);
	}

	protected void drawGUI(float frameAverage, long renderTime, long logicTime) {
		Graphics2D g2d = d.getGraphics();
		g2d.setFont(new Font("SanSerif", Font.PLAIN, 12));
		g2d.setColor(Color.orange);
		g2d.drawString("FPS: "+(int)(1/frameAverage),10,40);
		g2d.drawString("Arbiters: "+world.getArbiters().size(),10,60);
		g2d.drawString("Bodies: "+world.getBodies().size(),10,80);
		g2d.drawString("Render time: "+renderTime+"ms",10,100);
		g2d.drawString("Logic time: "+logicTime+"ms",10,120);
		if (ship.canExplode()) {
			g2d.setColor(Color.black);
			g2d.drawString("Score: " + score,width/2-27,height/2+5);
		}
		g2d.setColor(Color.gray);
		int w = width, h = height;
		g2d.drawString("Armor: " + (int)(ship.getDamage()*1000)/10+"%",w-120,h-95);
		g2d.drawString("Speed: " + ship.getVelocity().length(),w-120,h-75);
		g2d.drawString("Xcoord: " + (int)(xo - width/2),w-120,h-55);
		g2d.drawString("Ycoord: " + (int)(-yo + height/2),w-120,h-35);
		g2d.drawString("Asteroids: " + score,w-120,h-15);
		g2d.setColor(Color.red);
		g2d.drawString("left right up down space",15,h-35);
		g2d.drawString("R - Restart Demo",15,h-15);
		g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
	}

	/**
	 * Creates/deletes asteroids, manages ship position.
	 */
	protected void update() {
		double xmax = xo + width + border + buf;
		double xmin = xo - border - buf;
		double ymax = yo + height + border + buf;
		double ymin = yo - border - buf;
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			double x = body.getPosition().getX();
			double y = body.getPosition().getY();
			if (x > xmax || x < xmin || y > ymax || y < ymin) {
				score++;
				world.remove(body);
				if (world.getBodies().size() <= numrocks)
					world.add(newAsteroid());
			}
		}
		xo = ship.getPosition().getX() - (float)width/2;
		yo = ship.getPosition().getY() - (float)height/2;
		d.setCenter(v(xo, yo));
	}

	protected Asteroid newAsteroid() {
		// difficulty increases with score
		float vx = (float)((1+score/100)*(5 - Math.random()*10));
		float vy = (float)((1+score/100)*(5 - Math.random()*10));
		Asteroid rock;
		switch ((int)(5*Math.random())) {
			case 1: rock = new HexAsteroid(range(20,30)); break;
			case 2: rock = new Rock2(range(20,30)); break;
			default: rock = new CircleAsteroid(range(20,30)); break;
		}
		if (oneIn(200))
			rock = new CircleAsteroid(range(100,300));
		rock.setRestitution(0.2f);
		rock.adjustAngularVelocity((float)(2*Math.random()-1));
		Vector2f vo = getOffscreenCoords(rock.getRadius());
		rock.setPosition(vo.getX(), vo.getY());
		rock.adjustVelocity(v(vx, vy));
		return rock;
	}

	/**
	 * Get offscreen coords for a shape of radius r.
	 */
	protected Vector2f getOffscreenCoords(float r) {
		float x = 1, y = 1;
		// this is centered about the screen origin
		while(onScreen(v(x,y),r)) {
			x = (float)(Math.random()*2*(width + border) - width - border);
			y = (float)(Math.random()*2*(height + border) - height - border);
		}
		return v(x+xo+width/2+r,y+yo+height/2+r);
	}

	// precondition: v is absolute vector from display origin
	public boolean onScreen(ROVector2f v, float r) {
		float w2 = (width/2 + r);
		float h2 = (height/2 + r);
		float x = v.getX();
		float y = v.getY();
		return x > -w2-r && x < w2 && y > -h2-r && y < h2;
	}
}
