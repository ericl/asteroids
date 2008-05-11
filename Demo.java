import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
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
	protected Frame frame;
	protected Display d;
	protected boolean running;
	protected World world;
	protected float border = 300, buf = 500;
	protected int numrocks = 50, score;
	protected String stopped;
	protected Ship ship;
	protected float xid, xo, yo;
	protected int width, height;
	protected int shift, accel;
	protected float adjustAngularVelocity;

	public static void main(String[] args) {
		Demo demo = new Demo();
	}

	public Demo() {
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		frame = new Frame("Asteroid Field Demo");
		width = 500;
		height = 500;
		frame.setSize(width, height);
		running = true;
		
		int x = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getWidth()-width)/2;
		int y = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getHeight()-width)/2;
		
		frame.setLocation(x,y);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 27) {
					running = false;
					System.exit(0);
				}
				keyHit(e.getKeyChar());
			}
			public void keyReleased(KeyEvent e) {
				keyUnHit(e.getKeyChar());
			}
		});
	
		d = new Java2DDisplay(frame);
		d.setBackground("pixmaps/opo9929b.jpg");
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
		float target = 1000 / 60.0f;
		float frameAverage = target;
		long lastFrame = System.currentTimeMillis();
		float yield = 10000f, damping = 0.1f;
		long renderTime = 0, logicTime = 0;
		while (running) {
			long timeNow = System.currentTimeMillis();
			frameAverage = (frameAverage * 10 + (timeNow - lastFrame)) / 11;
			lastFrame = timeNow;
			
			yield+=yield*((target/frameAverage)-1)*damping+0.05f;
			for(int i=0;i<yield;i++)
				Thread.yield();
			
			long beforeRender = System.currentTimeMillis();
			d.drawWorld(world);
			drawGUI(frameAverage, renderTime, logicTime);
			d.show();
			ship.decrThrust();
			processKeys();
			renderTime = System.currentTimeMillis() - beforeRender;
			
			long beforeLogic = System.currentTimeMillis();
			for (int i=0;i<5;i++)
				world.step();
			logicTime = System.currentTimeMillis() - beforeLogic;
			update();
		}
	}

	/**
	 * Resets game within demo.
	 */
	protected void init() {
		world.clear();
		adjustAngularVelocity = 0;
		shift = 0;
		accel = 0;
		xid = (float)Math.E;
		d.setCenter(v(xo, yo));
		stopped = null;
		score = 0;
		world.setGravity(0,0);
		for (int i=0; i < numrocks; i++)
			world.add(newAsteroid());
		ship = new Ship(1000f);
		ship.setPosition((float)(xo+width/2),(float)(yo+height/2));
		ship.setRotDamping(1500);
		ship.adjustVelocity(v(0,0));
		ship.setMaxVelocity(100,100);
		world.add(ship);
	}

	/**
	 * Draws info text onscreen.
	 */
	protected void drawGUI(float frameAverage, long renderTime, long logicTime) {
		Font f = new Font("SanSerif", Font.PLAIN, 12);
		Color c = Color.orange;
		d.drawString("FPS: "+(int)(1000/frameAverage),f,c,v(10,40));
		d.drawString("Arbiters: "+world.getArbiters().size(),f,c,v(10,60));
		d.drawString("Bodies: "+world.getBodies().size(),f,c,v(10,80));
		d.drawString("Render time: "+renderTime+"ms",f,c,v(10,100));
		d.drawString("Logic time: "+logicTime+"ms",f,c,v(10,120));
		if (stopped != null) {
			c = Color.black;
			d.drawString(stopped,f,c,v(width/2-27,height/2+5));
		}
		c = Color.gray;
		int w = width, h = height;
		d.drawString("Speed: " + ship.getVelocity().length(),f,c,v(w-120,h-75));
		d.drawString("Xcoord: " + (int)(xo - 250),f,c,v(w-120,h-55));
		d.drawString("Ycoord: " + (int)(-yo + 250),f,c,v(w-120,h-35));
		d.drawString("Asteroids: " + score,f,c,v(w-120,h-15));
		c = Color.red;
		d.drawString("R - Restart Demo",f,c,v(15,h-15));
		f = new Font("Monospaced", Font.PLAIN, 14);
		d.drawString("    u",f,c,v(15,h-50));
		d.drawString("H h j k K",f,c,v(15,h-35));
	}

	/**
	 * Creates/deletes asteroids, manages ship position.
	 */
	protected void update() {
		if (xid != (float)Math.E && ship.getVelocity().getX() != xid) {
			world.remove(ship);
			stopped = "Score: " + score;
		}
		double xmax = xo + width + border + buf;
		double xmin = xo - border - buf;
		double ymax = yo + height + border + buf;
		double ymin = yo - border - buf;
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			if (body instanceof Asteroid) {
				double x = body.getPosition().getX();
				double y = body.getPosition().getY();
				if (x > xmax || x < xmin || y > ymax || y < ymin) {
					score++;
					world.remove(body);
					if (world.getBodies().size() < 51)
						world.add(newAsteroid());
				}
			}
		}

		xo = ship.getPosition().getX() - (float)width/2;
		yo = ship.getPosition().getY() - (float)height/2;
		d.setCenter(v(xo, yo));
		xid = ship.getVelocity().getX();
	}


	protected void keyOnceHit(char c) {
		if (c == 'r')
			init();
	}

	protected void keyHit(char c) {
		switch (c) {
			case 'h': adjustAngularVelocity = -.25f; return;
			case 'k': adjustAngularVelocity = .25f; return;
			case 'H': shift = -1; return;
			case 'K': shift = 1; return;
			case 'u': accel = 1; return;
			case 'j': accel = -1; return;
			case 'r': init(); return;
		}
	}

	protected void keyUnHit(char c) {
		switch (c) {
			case 'h': adjustAngularVelocity = 0; return;
			case 'k': adjustAngularVelocity = 0; return;
			case 'H': shift = 0; return;
			case 'K': shift = 0; return;
			case 'u': accel = 0; return;
			case 'j': accel = 0; return;
		}
	}

	protected void processKeys() {
		if (accel != 0)
			accel(accel);
		if (shift != 0)
			shift(shift);
		if (adjustAngularVelocity != 0)
			ship.adjustAngularVelocity(adjustAngularVelocity);
	}

	protected void accel(int accelfactor) {
		if (accelfactor > 0)
			ship.incrThrust();
		double ax = accelfactor*Math.sin(ship.getRotation());
		double ay = accelfactor*Math.cos(ship.getRotation());
		ship.adjustVelocity(v(ax,-ay));
		xid = ship.getVelocity().getX();
	}

	protected void shift(float dir) {
		double ax = Math.sin(ship.getRotation() + Math.PI/2);
		double ay = Math.cos(ship.getRotation() + Math.PI/2);
		ship.adjustVelocity(v(dir*ax,dir*-ay));
		xid = ship.getVelocity().getX();
	}

	protected Asteroid newAsteroid() {
		// difficulty increases with score
		float vx = (float)((1+score/100)*(5 - Math.random()*10));
		float vy = (float)((1+score/100)*(5 - Math.random()*10));
		Asteroid rock;
		switch ((int)(20*Math.random())) {
			case 0: rock = CircleAsteroid.random(0,300); break;
			case 1: rock = new Rock1(); break;
			case 2: rock = Sphere1.random(20,30); break;
			default: rock = CircleAsteroid.random(20,30); break;
		}
		if (oneIn(100))
			rock = HexAsteroid.getInstance();
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
		return (num*Math.random() < 1);
	}

	protected Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}

	// precondition: v is absolute vector from display origin
	public boolean onScreen(ROVector2f v, float r) {
		float w2 = (float)(width/2 + r);
		float h2 = (float)(height/2 + r);
		float x = v.getX();
		float y = v.getY();
		return x > -w2-r && x < w2 && y > -h2-r && y < h2;
	}
}
