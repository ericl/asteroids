package asteroids.bodies;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Ship2 extends Ship {

	public Ship2(World w) {
		super(w);
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
		setPosition(-300,0);
	}
}
