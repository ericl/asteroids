package asteroids.weapons;
import asteroids.display.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;

/**
 * Weapons should leave explosion remnants.
 */
public abstract class Explosion extends Body implements Textured {
	public Explosion() {
		super(new Circle(1), 1);
		setEnabled(false);
	}
	public abstract boolean dead();
}
