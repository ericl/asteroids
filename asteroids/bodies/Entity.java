/**
 * Small satellite that shoots ships.
 */

package asteroids.bodies;

import java.awt.Color;

import java.util.*;

import asteroids.ai.*;

import asteroids.display.*;

import asteroids.weapons.*;

import net.phys2d.raw.*;

public interface Entity extends Targetable, Automated, Drawable, Enhancable, CauseOfDeath, Textured, Body, Explodable {

	public void gainBeams(int add);
	public void setWeaponType(Weapon w);
	public void setAI(AI ai);
	public AI getAI();
	public String getCause();
	public long cloakTime();
	public void cloak();
	public void uncloak();
	public void startFiring();
	public void stopFiring();
	public void startLaunching();
	public void stopLaunching();
	public boolean launchMissile();
	public int numMissiles();
	public int numBeams();
	public void selfDestruct();
	public boolean dead();
	public void setAccel(float accel);
	public boolean fire(float rotation);
	public float getWeaponSpeed();
	public double health();
	public double shieldInfo();
	public void modifyTorque(float t);
	public void raiseShields();
	public void registerShield(Shield shield);
	public boolean weaponsMaxed();
	public void endFrame();
	public Body getRemnant();
	public boolean isVisible();
	public boolean targetableBy(Object o);
	public Color getColor();
	public boolean canExplode();
	public void collided(CollisionEvent event);
	public String killer();
	public List<Body> getFragments();
	public void gainInvincibility(int time, int warn);
	public void upgradeWeapons();
	public void setHealth(float health);
	public World getWorld();
	public void addMissiles(int num);
	public boolean isInvincible();
	public int getPointValue();
}
