package asteroids;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.*;
import java.awt.image.*;
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
	protected final Display display;
	protected final JFrame frame;
	protected final World world;

	public AbstractGame(String title, int w, int h) {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(2*w,h);
		Canvas a, b;
		JSplitPane jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                           a = new Canvas(), b = new Canvas());
		a.setSize(w,h);
		b.setSize(w,h);
		jsplit.setSize(w*2, h);
		jsplit.setDividerLocation(.5);
		jsplit.setVisible(true);
		frame.add(jsplit);
		frame.setLocationByPlatform(true);
		frame.addKeyListener(this);
		display = new Display(frame, jsplit);
		world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		world.addListener(new Exploder(world, display));
	}

	protected void mainLoop() {
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
		preWorld(display);
		display.drawWorld(world);
		postWorld(display);
	}

	protected void preWorld(Display display) {}
	protected void postWorld(Display display) {}
	protected abstract void update();
}
