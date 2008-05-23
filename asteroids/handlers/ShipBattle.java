package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class ShipBattle extends Field {
	protected final int MIN_DENSITY = 10;

	public ShipBattle(World w, Vector2f wxh, Ship[] shiparray, String id) {
		super(w, wxh, shiparray, id);
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
				ship.reset();
				world.add(ship);
			}
	}
}
