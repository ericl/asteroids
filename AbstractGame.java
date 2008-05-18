import javax.swing.JFrame;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;

public abstract class AbstractGame {
	private Display display;
	private JFrame frame;
	private World world;

	public void mainLoop() {
		Timer timer = new Timer(60f);
		while (true) synchronized (world) {
			doPhysics(timer.tick());
			doGraphics();
			doInput();
		}
	}

	private void doPhysics(float timestep) {
		for (int i=0; i < 5; i++)
			world.step(timestep);
	}

	protected abstract void doGraphics();
	protected abstract void doInput();
}
