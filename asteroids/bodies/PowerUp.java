package asteroids.bodies;
import asteroids.weapons.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import java.util.*;

public abstract class PowerUp extends Body implements Explodable {

	public static PowerUp random() {
		if (oneIn(5))
			return new Invincibility();
		return oneIn(2) ? new WeaponPower() : new ArmorRecovery();
	}

	public PowerUp(Polygon shape) {
		super(shape, shape.getArea());
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Ship)
			up((Ship)other);
	}

	public PowerUp(DynamicShape shape) {
		super(shape, 1e-10f);
	}

	public abstract void up(Ship ship);

	public abstract float getRadius();

	public Body getRemnant() {
		return new PowerUpExplosion();
	}

	public List<Body> getFragments() {
		return null;
	}
	
	public boolean canExplode() {
		return true;
	}
}
