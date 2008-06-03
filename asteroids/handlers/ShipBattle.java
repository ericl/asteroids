package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import net.phys2d.raw.*;

public class ShipBattle extends Field {

	public ShipBattle(World w, Display d, Ship[] shiparray, String id) {
		super(w, d, shiparray, id);
	}

	public boolean done() {
		return false;
	}

	public int score() {
		return count;
	}

	private class ShipHelper extends Thread {
		Ship ship;
		public ShipHelper(Ship s, World w) {
			ship = s;
		}
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {}
			ship.reset();
			world.add(ship);
			try {
				Thread.sleep(5000);
			} catch (Exception e) {}
			ship.setInvincible(false);
		}
	}


	public void update() {
		super.update();
		for (Ship ship : ships)
			if (ship.canExplode()) {
				ship.deaths++;
				ship.setInvincible(true);
				new ShipHelper(ship, world).start();
			}
	}
}
