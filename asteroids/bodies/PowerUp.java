package asteroids.bodies;
import asteroids.weapons.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import java.util.*;

public abstract class PowerUp extends Body implements Explodable {
	protected boolean activated;

	public static PowerUp random() {
		return new ArmorRecovery();
	}

	public PowerUp(Polygon shape) {
		super(shape, shape.getArea());
	}	

	public void collided(CollisionEvent e) {
		Body a = e.getBodyA();
		Body b = e.getBodyB();
		if (a instanceof Ship) {
			activated = true;
			up((Ship)a);
		}
		if (b instanceof Ship) {
			activated = true;
			up((Ship)b);
		}
	}

	public PowerUp(DynamicShape shape) {
		super(shape, 1e-10f);
	}

	public abstract void up(Ship ship);

	public abstract float getRadius();

	public Body getRemnant() {
		if (activated)
			return new PowerUpExplosion();
		else
			return null;
	}

	public List<Body> getFragments() {
		return null;
	}
	
	public boolean canExplode() {
		return true;
	}
}
