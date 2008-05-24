package asteroids;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import asteroids.bodies.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public abstract class AbstractGame extends KeyAdapter {
	protected Display display;
	protected JFrame frame;
	protected World world;

	public AbstractGame(String title, int w, int h) {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(w,h);
		frame.setLocationByPlatform(true);
		frame.addKeyListener(this);
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		world.enableRestingBodyDetection(.1f, .1f, .1f);
	}

	// makes window visible
	public void init() {
		display = new BasicDisplay(frame);
		world.addListener(new Exploder(world, display));
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display d) {
		display = d;
	}

	protected void mainLoop() {
		if (display == null)
			throw new IllegalStateException("Display not initialized.");
		Timer timer = new Timer(60f);
		float dt;
		while (true) {
			dt = timer.tick();
			synchronized (world) {
				update();
				doGraphics();
				doPhysics(dt);
			}
		}
	}

	private void doPhysics(float timestep) {
		for (int i=0; i < 5; i++)
			world.step(timestep);
	}

	private void doGraphics() {
		display.show();
		preWorld();
		display.drawWorld(world);
		postWorld();
	}

	protected void preWorld() {}
	protected void postWorld() {}
	protected abstract void update();
}
