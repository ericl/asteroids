/**
 * Body that adjust ship attributes on collision.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.*;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

public abstract class PowerUp extends TexturedPolyBody implements Explodable {
	protected boolean explode;

	public PowerUp(ROVector2f[] raw, String img, float nativesize, float size) {
		super(raw, img, nativesize, size);
		addBit(BIT_SHIELD_PENETRATING);
		setDamping(1);
		setMaxVelocity(20, 20);
	}

	public static PowerUp random() {
		if (Entity.weaponsAreMaxed) {
			switch ((int)(16*Math.random())) {
				case 0:
				case 1: return new Invincibility();
				case 2:
				case 3: return new ArmorRecovery();
				case 4:
				case 5:
				case 6: return new MissilePower();
				case 7:
				case 8:
				case 9: return new BeamPower();
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				default: return new ShieldRecovery();
			}
		}
		switch ((int)(20*Math.random())) {
			case 0:
			case 1: return new Invincibility();
			case 2:
			case 3: return new ArmorRecovery();
			case 4:
			case 5:
			case 6: return new MissilePower();
			case 7:
			case 8:
			case 9: return new WeaponPower();
			case 10:
			case 11:
			case 12: return new BeamPower();
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			default: return new ShieldRecovery();
		}
	}

	public Color getColor() {
		return Color.GREEN;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Enhancable) {
			up((Enhancable)other);
			explode = true;
		}
	}

	/**
	 * @param	ship	The ship to be powered up.
	 */
	protected abstract void up(Enhancable ship);

	public Body getRemnant() {
		return new PowerUpExplosion(Explosion.TrackingMode.TARGET);
	}

	public List<Body> getFragments() {
		return null;
	}
	
	public boolean canExplode() {
		return explode;
	}
}
