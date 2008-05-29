package asteroids.test;
import asteroids.display.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import asteroids.handlers.Timer;

public class Demo {
	protected JFrame frame;
	protected Display d;
	protected Europa object;
	protected long maxRenderTime = 0, maxLogicTime = 0,
		renderTime = 0, logicTime = 0, beforeRender, beforeLogic;
	protected Average render, logic, fps, arbiters, bodies;
	protected long numFrames = 0;
	protected World world;
	protected float border = 300, buf = 500;
	protected int numrocks = 30, count;
	protected String score;
	protected Ship ship;
	protected float xo, yo;
	protected int width, height;
	private boolean failed;
	private FiniteStarField k;

	private class Average {
		private double a;
		private int n;

		public void add(double x) {
			n++;
			a -= a/n - x/n;
		}

		public double getAvg() {
			return a;
		}
	}

	public static void main(String[] args) {
		new Demo();
	}

	public Demo() {
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		// trigger endFrame() events
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		frame = new JFrame("Asteroid Field Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setSize(width, height);
		frame.setMaximizedBounds(
			new Rectangle(frame.getToolkit().getScreenSize()));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationByPlatform(true);
		frame.setUndecorated(true);

		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'r': init(); return;
				}
			}
		});
		d = new BasicDisplay(frame, new Dimension(width, height));
		world.addListener(new Exploder(world, d));
		init();
		mainLoop();
	}

	protected void mainLoop() {
		Timer t = new Timer(60f);
		float dt;
		while (true) {
			// do the sleeping outside the synchronized part
			dt = t.tick();
			synchronized (world) {
				if (object.getRemnant().canExplode())
					failed = true;
				logic.add(logicTime);
				render.add(renderTime);
				if (renderTime > maxRenderTime)
					maxRenderTime = renderTime;
				if (logicTime > maxLogicTime)
					maxLogicTime = logicTime;
				beforeRender = System.currentTimeMillis();
				d.drawWorld(world);
				drawGUI(dt);
				d.show();
				renderTime = System.currentTimeMillis() - beforeRender;
				beforeLogic = System.currentTimeMillis();
				for (int i=0;i<5;i++)
					world.step(dt);
				logicTime = System.currentTimeMillis() - beforeLogic;
				update();
			}
		}
	}

	/**
	 * Resets game within demo.
	 */
	protected void init() {
		maxRenderTime = 0;
		maxLogicTime = 0;
		logic = new Average();
		fps = new Average();
		render = new Average();
		arbiters = new Average();
		bodies = new Average();
		synchronized (world) {
			failed = false;
			world.clear();
			frame.removeKeyListener(ship);
			score = null;
			xo = width/2;
			yo = height/2;
			d.setCenter(v(width, height));
			count = 0;
			world.setGravity(0,0);
			for (int i=0; i < numrocks; i++)
				world.add(newAsteroid());
			world.add(object = new Europa());
			object.setPosition((xo+width/2),(yo+height/2));
			ship = new Ship(world);
			k = new FiniteStarField(d);
			k.init();
			object.getRemnant().addExcludedBody(ship);
			ship.addExcludedBody(object);
			ship.setInvincible(true);
			frame.addKeyListener(ship);
			ship.setPosition((xo+width/2),(yo+height/2));
			world.add(ship);
		}
	}

	protected void drawGUI(float frameAverage) {
		Graphics2D g2d = d.getGraphics();
		if ((1/frameAverage) < 1000)
			fps.add(1/frameAverage);
		arbiters.add(world.getArbiters().size());
		bodies.add(world.getBodies().size());
		g2d.setFont(new Font("SanSerif", Font.PLAIN, 12));
		g2d.setColor(Color.orange);
		g2d.drawString("Avg FPS: "+(int)fps.getAvg()+"fps",10,20);
		g2d.drawString("Avg Render time: "+(int)render.getAvg()+"ms",10,40);
		g2d.drawString("Avg Logic time: "+(int)logic.getAvg()+"ms",10,60);
		g2d.drawString("Avg Arbiters: "+(int)arbiters.getAvg(),10,80);
		g2d.drawString("Avg Bodies: "+(int)bodies.getAvg(),10,100);
		g2d.drawString("Max Render time: "+maxRenderTime+"ms",10,120);
		g2d.drawString("Max Logic time: "+maxLogicTime+"ms",10,140);
		if (ship.canExplode() || failed) {
			world.remove(ship);
			g2d.setColor(Color.black);
			if (score == null)
				score = "" + count;
			g2d.drawString("Score: " + score,width/2-27,height/2+5);
		}
		int w = width, h = height;
		g2d.setColor(Color.red);
		g2d.drawString("Europa: " + object.getPercentDamage(),w-120,h-115);
		g2d.setColor(Color.gray);
		g2d.drawString("Armor: " + (ship.getDamage()*1000)/10,w-120,h-95);
		g2d.drawString("Speed: " + ship.getVelocity().length(),w-120,h-75);
		g2d.drawString("Xcoord: " + (int)(xo - width/2),w-120,h-55);
		g2d.drawString("Ycoord: " + (int)(-yo + height/2),w-120,h-35);
		g2d.drawString("Asteroids: " + count,w-120,h-15);
		g2d.setColor(Color.red);
		g2d.drawString("left right up down space",15,h-35);
		g2d.drawString("R - Restart Demo",15,h-15);
		g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
	}

	/**
	 * Creates/deletes asteroids, manages ship position.
	 */
	protected void update() {
		k.starField();
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
				if (body != object) {
					count++;
					world.remove(body);
					numrocks = 30+count/100;
					if (world.getBodies().size() <= numrocks)
						world.add(newAsteroid());
				}
			}
		}
		xo = ship.getPosition().getX() - (float)width/2;
		yo = ship.getPosition().getY() - (float)height/2;
		d.setCenter(ship.getPosition());
	}

	protected Asteroid newAsteroid() {
		// difficulty increases with count
		float vx = (float)((5+count/150)*(5 - Math.random()*10));
		float vy = (float)((5+count/150)*(5 - Math.random()*10));
		Asteroid rock;
		switch ((int)(5*Math.random())) {
			case 1: rock = new HexAsteroid(range(20,30)); break;
			case 2: rock = new Rock2(range(20,30)); break;
			default: rock = new CircleAsteroid(range(20,30)); break;
		}
		int chance = 100-count/30;
		if (oneIn(chance < 7 ? 7 : chance))
			rock = new CircleAsteroid(range(100,300));
//		// for easy visualization, ok?
//		if (rock instanceof CircleAsteroid)
//			((CircleAsteroid)rock).setColor(randomColor().darker().darker());
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
