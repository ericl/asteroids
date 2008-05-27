package asteroids;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import net.phys2d.raw.*;
import net.phys2d.raw.strategies.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public abstract class AbstractGame extends KeyAdapter {
	protected Display display;
	protected JFrame frame;
	protected World world;
	protected Pauser focus;
	protected final Dimension dim;
	protected volatile boolean pause;
	private MainLoop mainLoop;

	private class MainLoop extends Thread {
		public void run() {
			if (display == null)
				throw new IllegalStateException("Display not initialized.");
			Timer timer = new Timer(60f);
			float dt;
			while (true) {
				dt = timer.tick();
				while (pause) try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					timer.reset();
				}
				synchronized (world) {
					update();
					doGraphics();
					doPhysics(dt);
				}
			}
		}
	}

	public AbstractGame(String title, Dimension d) {
		dim = d;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(dim);
		frame.setLocationByPlatform(true);
		frame.addKeyListener(this);
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		mainLoop = new MainLoop();
		focus = new Pauser(frame, this);
		display = makeDisplay();
		frame.addFocusListener(focus);
		world.addListener(new Exploder(world, display));
	}

	public void mainLoop() {
		mainLoop.start();
	}

	protected Display makeDisplay() {
		return new BasicDisplay(frame, dim);
	}

	public void unpause() {
		if (pause) {
			pause = false;
			mainLoop.interrupt();
		}
	}

	public void pause() {
		if (!pause) {
			pause = true;
			synchronized (display) {
				Graphics2D g2d = display.getGraphics();
				g2d.setColor(new Color(100,100,100,100));
				g2d.fillRect(0,0,display.w(0),display.h(0));
				g2d.setFont(new Font("SanSerif", Font.BOLD, 15));
				g2d.setColor(Color.RED);
				g2d.drawString("PAUSED",20,display.h(-20));
				display.show();
			}
		}
	}

	protected void preWorld() {}
	protected void postWorld() {}
	protected void update() {}

	private void doPhysics(float timestep) {
		for (int i=0; i < 5; i++)
			world.step(timestep);
	}

	private void doGraphics() {
		synchronized (display) {
			display.show();
			preWorld();
			display.drawWorld(world);
			postWorld();
		}
	}
}