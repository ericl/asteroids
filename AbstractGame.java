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

public abstract class AbstractGame {
	protected final Display display;
	protected final JFrame frame;
	protected final World world;

	public AbstractGame(String title, int w, int h) {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(w,h);
		int x = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getWidth()-w)/2;
		int y = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getHeight()-h)/2;
		frame.setLocation(x,y);
		frame.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent event) {
				keyHit(event);	
			}
		});
		display = new Display(frame);
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
		preWorld();
		display.drawWorld(world);
		postWorld();
	}

	protected void preWorld() {}
	protected void postWorld() {}
	protected void keyHit(KeyEvent event) {}
	protected abstract void update();
}
