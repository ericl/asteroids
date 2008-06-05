package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
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
			world.remove(ship); // exploder should catch this.. doesn' always work (!)
			try {
				Thread.sleep(2000);
			} catch (Exception e) {}
			ship.setInvincible(true);
			ship.reset();
			if(!world.getBodies().contains(ship)) world.add(ship);
			try {
				Thread.sleep(3000);
			} catch (Exception e) {}
			ship.setInvincible(false);
			ship.waitingForSpawn = false;
		}
	}

	public void update() {
		super.update();
		for (Ship ship : ships)
			if (ship.canExplode() && !ship.waitingForSpawn) {
				ship.deaths++;
				ship.reset();
				ship.setPosition(ship.getPosition().getX()+range(-300,300),
				                 ship.getPosition().getY()+range(-300,300));
				world.add(ship);
				ship.waitingForSpawn = true;
				new ShipHelper(ship, world).start();
			}
	}
}
