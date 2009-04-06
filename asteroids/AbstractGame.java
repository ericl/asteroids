/**
 * Common game GUI and main loop.
 */

package asteroids;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import net.phys2d.raw.*;
import net.phys2d.raw.strategies.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public abstract class AbstractGame extends KeyAdapter implements WindowFocusListener {
	protected Display display;
	protected JFrame frame;
	protected World world;
	protected Stats stats;
	protected final Dimension dim;
	protected volatile boolean pause, running = true;
	private MainLoop mainLoop;
	private Exploder exploder;
	public final static Font FONT_BOLD = new Font("Serif", Font.BOLD, 15);
	public final static Font FONT_VERY_BOLD = new Font("Serif", Font.BOLD, 30);
	public final static Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
	public final static String RESTART_MSG = "R - Restart Game";
	public final static Color COLOR_BOLD = Color.ORANGE, COLOR = Color.lightGray;

	private class MainLoop extends Thread {
		long sleep_init, sleep_end, graphics_init, graphics_end, physics_init, physics_end, update_init, update_end;
		int steps;
		float dt;
		public void run() {
			Timer timer = new Timer(60f);
			while (running) {
				steps++;
				sleep_init = System.nanoTime();
				dt = timer.tick();
				sleep_end = System.nanoTime();
				while (pause) try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					timer.reset();
				}
				graphics_init = System.nanoTime();
				doGraphics();
				physics_init = graphics_end = System.nanoTime();
				doPhysics(dt);
				update_init = physics_end = System.nanoTime();
				update();
				update_end = System.nanoTime();
//				assert stat();
			}
		}

		public boolean stat() {
			if (steps % 100 == 0) {
				double sleep = (sleep_end - sleep_init) / 1e9 / dt;
				double graphics = (graphics_end - graphics_init) / 1e9 / dt;
				double physics = (physics_end - physics_init) / 1e9 / dt;
				double update = (update_end - update_init) / 1e9 / dt;
				System.out.println("\nFRAME " + steps);
				System.out.println("Sleep: " + percent(sleep));
				System.out.println("Graphics: " + percent(graphics));
				System.out.println("Physics: " + percent(physics));
				System.out.println("Update: " + percent(update));
			}
			return true;
		}

		private String percent(double frac) {
			return (int)(frac*1000)/10 + "%";
		}
	}

	protected int centerX(Font f, String s, Graphics2D g2d) {
		return (int)((dim.getWidth() - g2d.getFontMetrics(f)
				.getStringBounds(s, g2d).getWidth())/2);
	}

	public AbstractGame(String title, Dimension d) {
		dim = d;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(dim);
		frame.addKeyListener(this);
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		mainLoop = new MainLoop();
		display = makeDisplay();
		final KeyboardFocusManager manager =
		      KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getSource() instanceof Canvas) {
					manager.redispatchEvent(frame, e);
					return true;
				}
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
		if (!pause)
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
		exploder.endFrame();
	}

	private void doGraphics() {
		display.show();
		preWorld();
		display.drawWorld(world);
		postWorld();
	}
}
