package asteroids.bodies;
import net.phys2d.raw.*;
import java.awt.event.KeyEvent;
import asteroids.handlers.Stats;

public class Ship2 extends Ship {

	public Ship2(World w, Stats s) {
		super(w, s);
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
			case 'a': torque = -.00008f; break;
			case 'd': torque = .00008f; break;
			case 'w': accel = 10; break;
			case 's': accel = -5; break;
			case '`': fire = true; break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyChar()) {
			case 'a':
			case 'd': torque = 0; break;
			case 'w':
			case 's': accel = 0; break;
			case '`': fire = false; break;
		}
	}

	public void reset() {
		super.reset();
		setPosition(-100,0);
	}
}
