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
	protected String stopped;
	protected Ship ship;
	protected float xo, yo;
	protected int width, height;
	protected int accel;
	protected float adjustAngularVelocity;
	protected boolean fire;

	public static void main(String[] args) {
		Demo demo = new Demo();
	}

	public Demo() {
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		// trigger endFrame() events
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		world.addListener(new ShipKiller());
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
				if (e.getKeyCode() == 27) {
					running = false;
					System.exit(0);
				}
				keyHit(e.getKeyCode());
			}
			public void keyReleased(KeyEvent e) {
				keyUnHit(e.getKeyCode());
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
			processKeys();
			dt = t.tick();
			long beforeLogic = System.currentTimeMillis();
			for (int i=0;i<5;i++)
				world.step(dt);
			logicTime = System.currentTimeMillis() - beforeLogic;
			update();
		}
	}

	private class ShipKiller implements CollisionListener {
		public void collisionOccured(CollisionEvent event) {
			if (event.getBodyA() == ship || event.getBodyB() == ship) {
				if (!ship.survived(event.getPenetrationDepth())) {
					world.remove(ship);
					stopped = "Score: " + score;
				}
			}
		}
	}

	/**
	 * Resets game within demo.
	 */
	protected void init() {
		world.clear();
		adjustAngularVelocity = 0;
		accel = 0;
		xo = width/2;
		yo = height/2;
		d.setCenter(v(xo, yo));
		stopped = null;
		score = 0;
		world.setGravity(0,0);
		for (int i=0; i < numrocks; i++)
			world.add(newAsteroid());
		world.add(new Europa(150));
		ship = new Ship(1000f);
		ship.setPosition((xo+width/2),(yo+height/2));
		ship.setRotDamping(1500);
		ship.adjustVelocity(v(0,0));
		ship.setMaxVelocity(100,100);
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
		if (stopped != null) {
			g2d.setColor(Color.black);
			g2d.drawString(stopped,width/2-27,height/2+5);
		}
		g2d.setColor(Color.gray);
		int w = width, h = height;
		g2d.drawString("Armor: " + (int)(ship.getDamage()*1000)/10+"%",w-120,h-95);
		g2d.drawString("Speed: " + ship.getVelocity().length(),w-120,h-75);
		g2d.drawString("Xcoord: " + (int)(xo - width/2),w-120,h-55);
		g2d.drawString("Ycoord: " + (int)(-yo + height/2),w-120,h-35);
		g2d.drawString("Asteroids: " + score,w-120,h-15);
		g2d.setColor(Color.red);
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
				if (world.getBodies().size() < 51)
					world.add(newAsteroid());
			}
		}

		xo = ship.getPosition().getX() - (float)width/2;
		yo = ship.getPosition().getY() - (float)height/2;
		d.setCenter(v(xo, yo));
	}


	protected void keyOnceHit(char c) {
		if (c == 'r')
			init();
	}

	// XXX don't do this in the real game
	protected void keyHit(int c) {
		switch (c) {
			case 37: adjustAngularVelocity = -.25f; return; // left
			case 39: adjustAngularVelocity = .25f; return; // right
			case 38: accel = 1; return; // up
			case 40: accel = -1; return; // down
			case 82: init(); return; // r
			case 32: fire = true; return; // space
		}
	}

	protected void keyUnHit(int c) {
		switch (c) {
			case 32: fire = false; return;
			case 37: adjustAngularVelocity = 0; return;
			case 39: adjustAngularVelocity = 0; return;
			case 38: accel = 0; return;
			case 40: accel = 0; return;
		}
	}

	protected void processKeys() {
		if (accel != 0)
			accel(accel);
		if (adjustAngularVelocity != 0)
			ship.adjustAngularVelocity(adjustAngularVelocity);
		if (fire)
			world.add(ship.fire());
	}

	protected void accel(int accelfactor) {
		if (accelfactor > 0)
			ship.incrThrust();
		double ax = accelfactor*Math.sin(ship.getRotation());
		double ay = accelfactor*Math.cos(ship.getRotation());
		ship.adjustVelocity(v(ax,-ay));
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

	protected boolean oneIn(int num) {
		return num*Math.random() < 1;
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
