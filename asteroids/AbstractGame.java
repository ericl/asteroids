/**
 * Common game GUI and main loop.
 */

package asteroids;

import java.awt.*;

import java.awt.event.*;

import javax.swing.JFrame;

import asteroids.display.*;

import asteroids.handlers.*;

import net.phys2d.raw.*;

import net.phys2d.raw.strategies.*;

import static asteroids.Util.*;

public abstract class AbstractGame extends KeyAdapter implements WindowFocusListener {
	protected final int NUM_PLAYERS;
	protected boolean devmode;
	// where NONE = game intro, IMPOSSIBLE = resource starvation
	public enum Level {
		START(1), EASY(2), MEDIUM(3), HARD(4), BLUE(5), SWARM(6), DONE(7);

		private int level;

		private Level(int level) {
			this.level = level;
		}

		public int quantify() {
			return level;
		}

		public String toString() {
			return "Level " + level;
		}
	};
	public static Level globalLevel = Level.START;
	protected World world;
	protected Display display;
	protected JFrame frame;
	protected Stats stats;
	protected final Dimension dim;
	protected volatile boolean pause, running = true;
	private MainLoop mainLoop;
	private Exploder exploder;
	public final static Font FONT_BOLD = new Font("Serif", Font.BOLD, 15);
	public final static Font FONT_VERY_BOLD = new Font("Serif", Font.BOLD, 30);
	public final static Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
	public final static Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 9);
	public final static String RESTART_MSG = "R - Restart Game";
	public final static Color COLOR_BOLD = Color.ORANGE, COLOR = Color.lightGray;

	private void checkDiff(String section, long diff) {
		if (diff > 100)
			System.out.println("W: " + section + " took " + diff + " ms");
	}

	private class MainLoop extends Thread {
		int steps;
		float dt;
		public void run() {
			try {
				Timer timer = new Timer(60f);
				while (running) {
					long last = System.currentTimeMillis(), diff = 0;
					steps++;
					dt = timer.tick();
					if (devmode) {
						diff = System.currentTimeMillis() - last;
						checkDiff("tick", diff);
						last = System.currentTimeMillis();
					}
					while (pause) try {
						Timer.pauseMode = true;
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						timer.reset();
					}
					if (devmode) {
						diff = System.currentTimeMillis() - last;
						checkDiff("pause", diff);
						last = System.currentTimeMillis();
					}
					doGraphics();
					if (devmode) {
						diff = System.currentTimeMillis() - last;
						checkDiff("graphics", diff);
						last = System.currentTimeMillis();
					}
					doPhysics(dt);
					if (devmode) {
						diff = System.currentTimeMillis() - last;
						checkDiff("physics", diff);
						last = System.currentTimeMillis();
					}
					update();
					if (devmode) {
						diff = System.currentTimeMillis() - last;
						checkDiff("update", diff);
						last = System.currentTimeMillis();
					}
				}
			} catch (Throwable e) {
				oops(e);
			}
		}
	}

	public void newGame() {
		exploder.reset();
	}

	protected int centerX(Font f, String s, Graphics2D g2d) {
		return (int)((dim.getWidth() - g2d.getFontMetrics(f)
				.getStringBounds(s, g2d).getWidth())/2);
	}

	public AbstractGame(String title, Dimension d, int players) {
		NUM_PLAYERS = players;
		dim = d;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(dim);
		frame.addKeyListener(this);
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20, 5));

		/* IMPORTANT - enables endFrame() calls */
		world.enableRestingBodyDetection(.1f, .1f, .1f);

		mainLoop = new MainLoop();
		display = makeDisplay();
		final KeyboardFocusManager manager =
		      KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getSource() instanceof Canvas)
					manager.redispatchEvent(frame, e);
				return false;
			}
		});
		stats = new LocalStats();
		frame.addWindowFocusListener(this);
		exploder = new Exploder(world, display);
		world.addListener(exploder);
		exploder.addStatsListener(stats);
	}

	public void mainLoop() {
		mainLoop.start();
	}

	public void windowGainedFocus(WindowEvent e) {
		unpause();
	}

	public void windowLostFocus(WindowEvent e) {
		pause();
	}

	public void pause() {
		if (!pause && !devmode)
			pause = true;
	}

	public void unpause() {
		if (pause) {
			pause = false;
			mainLoop.interrupt();
		}
	}

	protected Display makeDisplay() {
		frame.setLocationByPlatform(true);
		Canvas a = new Canvas();
		frame.add(a);
		return new Display(frame, dim, a);
	}

	protected void preWorld() {}
	protected void postWorld() {}
	protected void update() {}

	private void doPhysics(float timestep) {
		for (int i=0; i < 5; i++)
			world.step(timestep);
		exploder.endFrame(timestep);
	}

	private void doGraphics() {
		display.show();
		preWorld();
		display.drawWorld(world);
		postWorld();
	}
}
