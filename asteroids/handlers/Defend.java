package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import net.phys2d.raw.*;

public class Defend extends Field {
	private Europa europa;
	public final static int DEFEND = 5;

	public Defend(World w, Display d, Ship ship) {
		super(w, d, ship, Field.ROCKY);
	}

	public Europa getObject() {
		return europa;	
	}

	public int getID() {
		return DEFEND;
	}

	public void start() {
		world.clear();
		europa = new Europa();	
		world.add(europa);
		for (int i=0; i < ships.length; i++) {
			ships[i].reset();
			ships[i].addExcludedBody(europa);
			world.add(ships[i]);
		}
		count = 0;
		score = -1;
	}

	protected Visible[] getTargets() {
		Visible[] targets = new Visible[ships.length+1];
		System.arraycopy(ships, 0, targets, 0, ships.length);
		targets[targets.length-1] = europa;
		return targets;
	}

	public void update() {
		if (europa.canExplode() && !ships[0].canExplode())
			display.setCenter(europa.getPosition());
		super.update();
	}

	public boolean done() {
		return super.done() || europa.canExplode();
	}
}
