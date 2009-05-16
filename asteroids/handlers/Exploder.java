/**
 * Listens for collision events and processes them accordingly.
 */

package asteroids.handlers;

import java.util.*;

import asteroids.ai.*;

import asteroids.bodies.*;

import asteroids.display.*;

import asteroids.weapons.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

public class Exploder implements CollisionListener {
	// queue of explosions that will likey be removed soon
	private Queue<Explosion> explosionQueue = new LinkedList<Explosion>();
	// prevent rare double/triple collisionevents from creating multiple explosions
	private Set<Body> exploded = new HashSet<Body>();
	// statistics listeners
	private List<Stats> stats = new LinkedList<Stats>();
	private World world;
	private Display display;
	private CollisionGrouper grouper;
	private List<Entity> dead = new LinkedList<Entity>();
	private final static int MAX_BODIES = 300;
	private final static float MAX_RADIAL_DEVIATION = 10, COLLIDE_BOUNDS = 150, MAX_J = 2;

	public void reset() {
		dead.clear();
	}

	/**
	 * phys2d will not collide bodies with any matching bits...
	 * we use this to stop runaway explosions...
	 * there are 63 bits/groups available for use so they wrap around
	 */
	private class CollisionGrouper {
		// 1l reserved for shield + powerup
		private long initmask = BIT_MIN_FREE;
		private long nextmask = initmask;

		/**
		 * @return	Bitmask of next available group.
		 */
		public long findGroup(Body b) {
			if (b.getBitmask() != 0)
				return b.getBitmask();
			nextmask = nextmask << 1;
			if (nextmask == 0)
				nextmask = initmask;
			return nextmask;
		}
	}

	public Exploder(World w, Display d) {
		world = w;
		display = d;
		grouper = new CollisionGrouper();
	}

	/**
	 * Clears the anti-explosion-duplication list.
	 */
	public void endFrame(float timestep) {
		exploded.clear();
		ListIterator<Entity> iter = dead.listIterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			if (e.dead() && !(e.getAI() instanceof HumanShipAI)) {
				iter.remove();
				world.remove(e);
			} else {
				Vector2f pos = sub(e.getPosition(), scale(e.getVelocity(), -5*timestep));
				e.setEnabled(true);
				if (e.getVelocity().length() < 5)
					e.adjustVelocity(negate(e.getVelocity()));
				else
					e.adjustVelocity(scale(e.getVelocity(), -.01f));
				e.setEnabled(false);
				e.setPosition(pos.getX(), pos.getY());
			}
		}
	}

	/**
	 * Add a Stats to the list of Stats.
	 * @param	s	The Stats to add.
	 */
	public void addStatsListener(Stats s) {
		stats.add(s);
	}

	/**
	 * Clears the queue of collisions so the world is not overloaded.
	 * @param	event	The collision event between two bodies.
	 */
	public void collisionOccured(CollisionEvent event) {
		while (!explosionQueue.isEmpty() && explosionQueue.peek().dead())
			world.remove(explosionQueue.remove());
		if (event.getBodyA() instanceof Explodable)
			((Explodable)event.getBodyA()).collided(event);
		if (event.getBodyB() instanceof Explodable)
			((Explodable)event.getBodyB()).collided(event);
		if (event.getBodyA() instanceof Explodable)
			tryExplode((Explodable)event.getBodyA(), event.getBodyB(), event);
		if (event.getBodyB() instanceof Explodable)
			tryExplode((Explodable)event.getBodyB(), event.getBodyA(), event);
	}

	/**
	 * Collision algorithm between the two bodies.
	 * @param	body	The body that has collided.
	 * @param	other	The other body that it has collided with.
	 * @param	event	The collision event between body one and two.
	 */
	private void tryExplode(Explodable e, Body other, CollisionEvent event) {
		if (exploded.contains(e)) // don't explode anything twice
			return;
		exploded.add(e);
		// don't explode some offscreen or non-exploding bodies
		if (!isStuck(e, other) && (!e.canExplode()
				|| !display.inView(e.getPosition(), e.getRadius()+COLLIDE_BOUNDS)
				&& !(e instanceof Weapon))) {
			return;
		}
		world.remove(e);
		if (e instanceof Entity)
			dead.add((Entity)e);
		if (other instanceof Weapon)
			for (Stats stat : stats)
				stat.kill(((Weapon)other).getOrigin(), e, event);
		else if (other instanceof Entity)
			for (Stats stat : stats)
				stat.kill((Entity)other, e, event);
		else if (other instanceof Shield)
			for (Stats stat : stats)
				stat.kill(((Shield)other).getSource(), e, event);
		Body rem = e.getRemnant();
		if (!(rem instanceof Explosion) && world.getBodies().size() > MAX_BODIES)
			return;
		List<Body> f = e.getFragments();
		long group = grouper.findGroup(e);
		if (rem instanceof Explosion) // gah, another special case
			explosionQueue.add((Explosion)rem);
		if (rem != null)
			rem.addBit(group);
		if (!(other instanceof Targetable || e instanceof Weapon))
			other.addBit(group);
		// user-related stuff automatically passes the group limit
		if (!(e instanceof Targetable || other instanceof Targetable
				|| other instanceof Weapon || rem instanceof Explosion)) {
			e.addBit(group);
		}
		// not really J
		float J = other.getMass() / e.getMass() *
				   (e.getRestitution() + other.getRestitution()) / 2;
		J = Math.min(MAX_J, J);
		Vector2f v = sub(e.getVelocity(),(scale(other.getVelocity(),-J)));
		if (e instanceof Entity) {
			e.adjustVelocity(negate(e.getVelocity()));
			e.adjustVelocity(v);
			e.setEnabled(false);
		}
		if (f != null) {
			for (Body b : f) {
				b.addBit(group);
				double theta = Math.random()*2*Math.PI;
				float sx = e.getPosition().getX();
				float sy = e.getPosition().getY();
				sx += e.getRadius() * (float)Math.sin(theta) / 2;
				sy -= e.getRadius() * (float)Math.cos(theta) / 2;
				sx += range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				sy -= range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				b.setRotation((float)(2 * Math.PI * Math.random()));
				b.adjustAngularVelocity(range(
				   -e.getAngularVelocity(), e.getAngularVelocity()));
				b.adjustVelocity(scale(direction(theta),
					(float)Math.sqrt(Math.random()*2*v.length())));
				b.adjustVelocity(v);
				b.setPosition(sx, sy);
				world.add(b);
			}
		}
		if (rem != null) {
			if (rem instanceof Explosion && !(e instanceof Targetable))
				rem.setPosition(event.getPoint().getX(),
					event.getPoint().getY());
			else
				rem.setPosition(e.getPosition().getX(),
					e.getPosition().getY());
			if (rem instanceof Explosion)
				((Explosion)rem).setTracking(e, other);
			rem.adjustVelocity(v);
			rem.setRotation(e.getRotation());
			rem.adjustAngularVelocity(e.getAngularVelocity());
			world.add(rem);
		}
	}

	/**
	 * @return	Damage done to a body.
	 */
	public static double getDamage(CollisionEvent e, Body victim) {
		Body other = e.getBodyA().equals(victim) ? e.getBodyB() : e.getBodyA();
		if (other instanceof Weapon)
			return ((Weapon)other).getDamage();
		else if (other instanceof PowerUp) // got killed this way before :(
			return 0;
		double vmod = sub(victim.getVelocity(), other.getVelocity()).lengthSquared();
		return Math.min(other.getMass(), victim.getMass()) * vmod / 1e7;
	}

	/**
	 * Checks if the two bodies are stuck.
	 * @param	body	First body.
	 * @param	other	Second body.
	 * @return	True if both bodies are stuck to each other.
	 */
	public boolean isStuck(Body body, Body other) {
		if (!(body instanceof Asteroid && other instanceof Asteroid))
			return false; // too risky to evaluate - might kill self
		Visible e = (Visible)body;
		float diff = sub(body.getPosition(),other.getPosition()).length();
		if (diff < e.getRadius() / 2)
			return true;
		return false;
	}
}
