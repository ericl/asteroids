package asteroids.ai;

import java.awt.event.*;
import java.awt.event.KeyEvent;

import net.phys2d.raw.World;

public class HumanShipAI extends ShipAI implements KeyListener {
	protected int delay;
	protected boolean delayAI, active;
	protected int activeTime;

	public HumanShipAI(World world, Automated ship, int delay, boolean delayAI) {
		super(world, ship);
		this.delay = delay;
		this.delayAI = delayAI;
		if (delayAI)
			activeTime = delay;
		ship.setAI(this);
	}

	public void update() {
		if (activeTime-- < 1)
			super.update();
		if (active)
			activeTime = delay;
	}

	public void reset() {
		super.reset();
		activeTime = delayAI ? delay : 0;
	}

	public void notifyInput(boolean down) {
		activeTime = delay;
		active = down;
	}

	public void keyTyped(KeyEvent e) {}
	
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
}
