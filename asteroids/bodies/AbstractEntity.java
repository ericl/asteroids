/**
 * Small satellite that shoots ships.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.*;

import asteroids.*;

import asteroids.ai.*;

import asteroids.handlers.*;
import asteroids.handlers.Timer;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.AbstractGame.Level.*;

import static asteroids.Util.*;

public abstract class AbstractEntity extends TexturedPolyBody implements Entity {
	protected static int CLOAK_DELAY = 250, CLOAK_MAX = 15000, BEAM_MIN = 30;
	protected int count = 0;
	protected String cause;
	protected long cloaktime = CLOAK_MAX, t = Timer.gameTime();
	protected WeaponSys missiles;
	protected Body killer;
	protected WeaponSys weapons, oldweapons;
	protected World world;
	protected Shield shield, oldshield;
	protected Color color = Color.ORANGE;
	protected Explosion explosion;
	protected int numMissiles;
	protected float damage, torque, accel;
	protected boolean fire, launch, destruct;
	protected int cloak = Integer.MAX_VALUE;
	protected AI ai;
	protected int textStatus = Integer.MAX_VALUE; // for blinking only
	protected long warningStart; // end of warning -> not invincible
	protected long invincibleEnd; // end of invincibility -> warning(warntime)
	protected long last = Timer.gameTime();
	protected boolean raiseShield = true;
	protected int beams;
	protected static Entity reference;

	public AbstractEntity(ROVector2f[] raw, String img, float nativesize, float size, float mass, World world, Weapon weapon) {
		super(raw, img, nativesize, size, mass);
		this.world = world;
		setRotDamping(mass*mass*mass/843750);
		weapons = new WeaponSys(this, world, weapon);
		missiles = new WeaponSys(this, world, new Missile(world));
		ai = new ShipAI(world, this);
	}

	public void gainBeams(int add) {
		beams += add;
	}

	public void setWeaponType(Weapon w) {
		weapons.setWeaponType(w);
	}

	public void setAI(AI ai) {
		this.ai = ai;
		if (ai != null)
			ai.reset();
	}

	public String getCause() {
		return "an unknown entity";
	}

	public long cloakTime() {
		return cloaktime;
	}

	public void cloak() {
		if (cloaktime > 1000)
			cloak = CLOAK_DELAY;
	}

	public void uncloak() {
		cloak = Integer.MAX_VALUE;
	}

	public void startFiring() {
		fire = true;
		count = BEAM_MIN;
	}

	public void stopFiring() {
		fire = false;
	}

	public void startLaunching() {
		launch = true;
	}

	public void stopLaunching() {
		launch = false;
	}

	public boolean launchMissile() {
		if (canExplode())
			return false;
		if (numMissiles > 0) {
			if (missiles.fire()) {
				cloak = Integer.MAX_VALUE;
				numMissiles--;
				return true;
			}
		}
		return false;
	}

	public int numMissiles() {
		return numMissiles;
	}

	public int numBeams() {
		return beams;
	}

	public void selfDestruct() {
		destruct = true;
		damage = getMaxArmor() + 1;
	}

	public boolean dead() {
		return destruct || canExplode() && explosion != null && explosion.dead();
	}

	protected void accel() {
		Vector2f dir = direction(getRotation());
		addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
	}

	public void setAccel(float accel) {
		this.accel = accel;
	}

	private boolean fire() {
		if (canExplode())
			return false;
		if (weapons.fire()) {
			cloak = Integer.MAX_VALUE;
			return true;
		}
		return false;
	}

	// directional - only for certain weapons
	public boolean fire(float rotation) {
		if (weapons.isBeam())
			return false;
		if (weapons.fire(rotation)) {
			cloak = Integer.MAX_VALUE;
			return true;
		}
		return false;
	}

	public float getWeaponSpeed() {
		return weapons.getWeaponSpeed();
	}

	public double health() {
		return Math.max(0, (getMaxArmor() - damage) / getMaxArmor());
	}

	public double shieldInfo() {
		return shield == null ? -1 : shield.health();
	}

	protected float getMaxArmor() {
		return 3;
	}

	protected void torque() {
		adjustAngularVelocity(getMass()*torque);
	}

	public void modifyTorque(float t) {
		torque = t;
	}

	public void raiseShields() {
		raiseShield = true;
	}

	protected Shield getShield() {
		return new Shield(this, world);
	}

	protected void updateShield() {
		if (isVisible()) {
			if (shield == null && oldshield != null) {
				shield = oldshield;
				world.add(shield);
			}
		} else {
			if (shield != null) {
				world.remove(shield);
				oldshield = shield;
				shield = null;
			}
		}
		if (raiseShield) {
			if (shield != null)
				world.remove(shield);
			world.add(shield = getShield());
			raiseShield = false;
		}
		if (shield != null) {
			oldshield = null;
			if (canExplode() || shield.canExplode()) {
				if (canExplode())
					killer(); // get message "while shielded"
				world.remove(shield);
				shield = null;
			}
		}
	}

	public void registerShield(Shield shield) {
		this.shield = shield;
	}

	public boolean weaponsMaxed() {
		if (oldweapons != null)
			return oldweapons.isMaxed();
		else
			return weapons.isMaxed();
	}

	public void endFrame() {
		long now = Timer.gameTime();
		super.endFrame();
		if (ai instanceof HumanShipAI)
			reference = this;
		updateShield();
		weapons.gc();
		if (oldweapons != null)
			oldweapons.gc();
		missiles.gc();
		float v = getVelocity().length();
		float limit = 50;
		setDamping(getMass() / 1500 * (v < limit ? 0 : v < limit*2 ? .1f : .5f));
		if (ai != null)
			ai.update();
		torque();
		cloak -= now - last;
		count--;
		if (weapons.isBeam()) {
			if ((fire || count > 0) && fire())
				beams--;
			else
				accel();
			if (beams <= 0 && oldweapons != null)
				weapons = oldweapons;
		} else {
			if (fire)
				fire();
			accel();
			if (beams > 0) {
				oldweapons = weapons;
				weapons = new WeaponSys(this, world, new Beam());
			}
		}
		if (launch)
			launchMissile();
		if (destruct)
			world.remove(this);
		if (!isVisible())
			cloaktime -= now - last;
		else
			cloaktime += (now - last) / 3;
		if (cloaktime < 0) {
			uncloak();
			cloaktime = 0;
		} else if (cloaktime > CLOAK_MAX)
			cloaktime = CLOAK_MAX;
		last = now;
	}

	public Body getRemnant() {
		updateShield();
		return explosion = new LargeExplosion(Explosion.TrackingMode.NONE, 1.5f);
	}

	public boolean isVisible() {
		return cloak > 0 || cloaktime == 0;
	}

	public boolean targetableBy(Object o) {
		return isVisible();
	}

	public Color getColor() {
		long time = Timer.gameTime();
		if (isInvincible()) {
			if (time < warningStart || textStatus-- % 10 > 5)
				return Color.GREEN;
		}
		if (health() < .2)
			return Color.RED;
		else if (health() < .6)
			return Color.YELLOW;
		return AbstractGame.COLOR;
	}

	public boolean canExplode() {
		return damage > getMaxArmor() && !isInvincible();
	}

	public void collided(CollisionEvent event) {
		if (!canExplode()) {
			killer = event.getBodyA();
			if (killer.equals(this))
				killer = event.getBodyB();
		}
		if (killer instanceof Cannon)
			if (shield != null)
				shield.cloak();
		if (!isInvincible())
			damage += Exploder.getDamage(event, this);
		updateShield();
	}

	public String killer() {
		if (cause != null)
			return cause;
		Object foo = killer; // do not modify killer!
		if (destruct) {
			if (AbstractGame.globalLevel == DONE)
				return "quit while ahead";
			else
				return "quit game";
		}
		else if (AbstractGame.globalLevel == DONE)
			return "ran out of levels";
		else if (killer instanceof Beam)
			return "blasted apart";
		else if (killer instanceof Missile)
			return "tracked down";
		else if (killer == null)
			return "died of unknown causes";
		else if (killer.equals(this))
			return "imploded";
		String prefix = "";
		if (killer instanceof Swarm) {
			prefix = "crushed by ";
		} else if (killer instanceof Weapon) {
			prefix = "shot by ";
			foo = ((Weapon)killer).getOrigin();
		} else if (killer instanceof Entity) {
			prefix = "collided with ";
		} else {
			prefix = "crashed into ";
		}
		String sub = "a " + foo.getClass().getName();
		if (foo instanceof CauseOfDeath) {
			sub = ((CauseOfDeath)foo).getCause();
		} else {
			sub = sub.substring(sub.lastIndexOf(".") + 1);
		}
		String suffix = "";
		if (isInvincible())
			suffix = " while invincible!?";
		else if (!isVisible())
			suffix = " while cloaked";
		else if (shield != null)
			suffix = " while shielded";
		return cause = prefix + sub + suffix;
	}

	public List<Body> getFragments() {
		double min = 3;
		double max = 6;
		if (getMass() > 1200)
			max += 2;
		int num = 10;
		num += getMass() / 750;
		List<Body> f = new ArrayList<Body>(num + 1);
		for (int i=0; i < num; i++)
			f.add(new SpaceDebris(range(min,max)));
		if (getMass() > 3000)
			for (int i=0; i < 3; i++)
				f.add(new SpaceDebris(range(10,15)));
		if (oneIn((int)(30/Math.sqrt(getPointValue()))))
			f.add(PowerUp.random());
		return f;
	}

	public void gainInvincibility(int time, int warn) {
		if (isInvincible())
			return;
		invincibleEnd = Timer.gameTime() + time;
		warningStart = invincibleEnd - warn;
	}

	public void upgradeWeapons() {
		if (oldweapons != null)
			oldweapons.upgrade();
		else
			weapons.upgrade();
	}

	public void setHealth(float health) {
		this.damage = getMaxArmor() - getMaxArmor() * health;
	}

	public World getWorld() {
		return world;
	}

	public void addMissiles(int num) {
		numMissiles += num;
	}

	public boolean isInvincible() {
		return invincibleEnd > Timer.gameTime();
	}

	public abstract int getPointValue();

	public String toString() {
		return ai.toString();
	}
}
