package asteroids.ai;

import java.awt.Dimension;

import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import net.phys2d.raw.*;
import net.phys2d.math.*;

import static net.phys2d.math.MathUtil.*;

import static asteroids.Util.*;

public class HumanShipAI extends ShipAI implements KeyListener, MouseListener {
	protected int delay, originalDelay;
	protected boolean delayAI, active;
	protected Dimension screen;
	protected int activeTime;

	public HumanShipAI(World world, Automated ship, int delay, boolean delayAI, Dimension screen) {
		super(world, ship);
		this.delay = originalDelay = delay;
		this.delayAI = delayAI;
		if (delayAI)
			activeTime = delay;
		ship.setAI(this);
		if (screen == null)
			screen = new Dimension();
		else
			this.screen = screen;
	}

	public void update() {
		if (activeTime-- < 1)
			super.update();
		if (active)
			activeTime = delay;
	}

	public void reset() {
		super.reset();
		delay = originalDelay;
		activeTime = delayAI ? delay : 0;
	}

	public void notifyInput(boolean down) {
		activeTime = delay;
		active = down;
		if (delay != Integer.MAX_VALUE)
			delay += 5;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT: ship.modifyTorque(-8e-5f); notifyInput(true); break;
			case KeyEvent.VK_RIGHT: ship.modifyTorque(8e-5f); notifyInput(true); break;
			case KeyEvent.VK_UP: ship.setAccel(7.5f); notifyInput(true); break;
			case KeyEvent.VK_DOWN: ship.setAccel(-3.75f); notifyInput(true); break;
			case KeyEvent.VK_SPACE: ship.startFiring(); notifyInput(true); break;
			case KeyEvent.VK_F: ship.startLaunching(); notifyInput(true); break;
			case KeyEvent.VK_Q: ship.selfDestruct(); break;
			case KeyEvent.VK_C:
				if (ship.canTarget())
					ship.cloak();
				else
					ship.uncloak();
				notifyInput(false); break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: ship.modifyTorque(0); notifyInput(false); break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN: ship.setAccel(0); notifyInput(false); break;
			case KeyEvent.VK_SPACE: ship.stopFiring(); notifyInput(false); break;
			case KeyEvent.VK_F: ship.stopLaunching(); notifyInput(false); break;
		}
	}

	public void mousePressed(MouseEvent e) {
		Vector2f ds = sub(scale(v(screen), .5f), v(e.getPoint()));
		double tFinal = Math.atan2(ds.getY(), ds.getX()) - Math.PI/2;
		ship.fire((float)tFinal);
	}

	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void keyTyped(KeyEvent e) {}
}
