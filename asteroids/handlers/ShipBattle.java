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

	public void update() {
		super.update();
		for (Ship ship : ships)
			if (ship.canExplode()) {
				ship.deaths++;
				ship.reset();
				world.add(ship);
			}
	}
}
