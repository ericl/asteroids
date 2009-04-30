package asteroids.bodies;

import java.util.ArrayList;
import java.util.List;

import asteroids.*;

import asteroids.ai.*;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;
import static asteroids.AbstractGame.Level.*;

public class Swarm extends Entity {
	protected static ROVector2f[] raw = {v(32,2), v(45,1), v(54,9), v(55,18), v(57,21), v(58,32), v(46,50), v(39,56), v(32,56), v(19,62), v(9,54), v(7,46), v(1,39), v(6,24), v(25,8)};

	public Swarm(World world) {
		super(raw, "pixmaps/3.png", 35, range(8,15), 1000, world, null);
		setAI(new HomingAI(world, this));
		setRotDamping(5000);
		if (AbstractGame.globalLevel != DONE)
			setMaxVelocity(50,50);
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		if (other instanceof Weapon)
			super.collided(e);
	}

	public String getCause() {
		if (AbstractGame.globalLevel == DONE)
			return "a malevolent force";
		else
			return "a heavy rock";
	}

	protected float getMaxArmor() {
		return .7f;
	}

	public Body getRemnant() {
		return null;
	}

	public int getPointValue() {
		return 50;
	}

	public boolean targetableBy(Object o) {
		return !(o instanceof Swarm);
	}

	public void endFrame() {
		super.endFrame();
		Vector2f dir = direction(getRotation());
		addForce(v(20*getMass()*dir.getX(),20*getMass()*dir.getY()));
	}

	protected void torque() {
		adjustAngularVelocity(10000*torque);
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(3);
		SmallAsteroid tmp;
		for (int i=0; i < 3; i++) {
			tmp = new SmallAsteroid(getRadius() / 3);
			f.add(tmp);
		}
		return f;
	}
}
