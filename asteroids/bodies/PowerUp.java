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

	private static PowerUp create() {
		switch ((int)(16*Math.random())) {
			case 0: return new MissilePower();
			case 1: return new BeamPower();
			case 2:
			case 3: return new Invincibility();
			case 4:
			case 5:
			case 6: return new ArmorRecovery();
			case 7:
			case 8:
			case 9:
			case 10: return new WeaponPower();
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			default: return new ShieldRecovery();
		}
	}

	public static PowerUp random() {
		PowerUp ret = create();
		if (AbstractEntity.reference == null)
			return ret;
		for (int i=0; i < 5; i++) {
			if (ret instanceof WeaponPower && AbstractEntity.reference.weaponsMaxed())
				ret = create();
			else if (ret instanceof ShieldRecovery && AbstractEntity.reference.shieldInfo() > .75)
				ret = create();
			else if (ret instanceof Invincibility && AbstractEntity.reference.isInvincible())
				ret = create();
			else if (ret instanceof BeamPower && AbstractEntity.reference.numBeams() > 4000)
				ret = create();
			else if (ret instanceof MissilePower && AbstractEntity.reference.numMissiles() > 45)
				ret = create();
			else
				break;
		}
		return ret;
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
