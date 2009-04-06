/**
 * Small satellite that shoots ships.
 */

package asteroids.bodies;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.*;

import asteroids.ai.*;
import asteroids.display.*;
import asteroids.handlers.*;
import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

import static asteroids.Util.*;

public class Satellite extends Body implements Targetable, Automated, Entity, Drawable {
	private WeaponSys weapons;
	protected Color color = Color.ORANGE;
	private final static float MAX = 3;
	private int deaths;
	private float damage, torque;
	private AI ai;

	public Satellite(World world) {
		super(new Circle(8), 1500);
		setRotDamping(4000);
		setColor(Color.RED);
		weapons = new WeaponSys(this, world, null);
		weapons.setRandomWeaponType();
		weapons.incrRandomWeaponLevel();
		ai = new AI(world, this) {
			public float getMaxTorque() {
				return 1e5f;
			}

			public float minTorqueThreshold() {
				return 1e4f;
			}
		};
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color c) {
		color = c;
	}

	public float getRadius() {
		return ((Circle)getShape()).getRadius();
	}

	public void reset() {
		BodyList excluded = getExcludedList();
		while (excluded.size() > 0)
			removeExcludedBody(excluded.get(0));
		setRotation(0);
		setPosition(0,0);
		adjustVelocity(MathUtil.sub(v(0,0),getVelocity()));
		adjustAngularVelocity(-getAngularVelocity());
		torque = 0;
		damage = 0;
		weapons.setRandomWeaponType();
		weapons.incrRandomWeaponLevel();
	}

	public void setAccel(float accel) { }
	public boolean launchMissile() { return false; }
	public int numDeaths() { return deaths; }
	public boolean isInvincible() { return false; }
	public int numMissiles() { return 0; }
	public boolean dead() { return canExplode(); }

	public boolean fire() {
		return weapons.fire();
	}

	public float getWeaponSpeed() {
		return weapons.getWeaponSpeed();
	}

	public double health() {
		return Math.max(0, (MAX - damage) / MAX);
	}

	public void modifyTorque(float t) {
		torque = t;
	}

	public void endFrame() {
		ai.update();
		setTorque(torque);
	}

	public Body getRemnant() {
		deaths++;
		return new LargeExplosion(1.5f);
	}

	public boolean canTarget() {
		return true;
	}

	public int getPointValue() {
		return 30;
	}

	public boolean canExplode() {
		return damage > MAX;
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}

	public List<Body> getFragments() {
		return null;
	}
}
