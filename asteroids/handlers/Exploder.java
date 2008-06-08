package asteroids.handlers;
import asteroids.weapons.*;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Exploder implements CollisionListener {
	private Queue<Explosion> explosionQueue = new LinkedList<Explosion>();
	private Set<Body> exploded = new HashSet<Body>();
	private World world;
	private Display display;
	private Stats stats;
	private CollisionGrouper grouper;

	static boolean DOUBLE_GROUP = true;
	static int MAX_BODIES = 300;
	static float MAX_RADIAL_DEVIATION = 10;
	static float COLLIDE_BOUNDS = 150;
	static float MAX_J = 2;

	/**
	 * phys2d will not collide bodies with any matching bits...
	 * we use this to stop runaway explosions...
	 * there are 63 bits/groups available for use so they wrap around
	 */
	private class CollisionGrouper {
		private long nextmask = 1l;

		/**
		 * @return bitmask of next available group
		 */
		public long findGroup(Body b) {
			if (b.getBitmask() != 0)
				return b.getBitmask();
			nextmask = nextmask << 1;
			if (nextmask == 0)
				nextmask = 1l;
			return nextmask;
		}
	}

	public Exploder(World w, Display d, Stats s) {
		world = w;
		display = d;
		stats = s;
		grouper = new CollisionGrouper();
	}

	public void endFrame() {
		exploded.clear();
	}

	public void collisionOccured(CollisionEvent event) {
		while (!explosionQueue.isEmpty() && explosionQueue.peek().dead())
			world.remove(explosionQueue.remove());
		if (event.getBodyA() instanceof Explodable)
			((Explodable)event.getBodyA()).collided(event);
		if (event.getBodyB() instanceof Explodable)
			((Explodable)event.getBodyB()).collided(event);
		if (event.getBodyA() instanceof Explodable)
			tryExplode(event.getBodyA(), event.getBodyB(), event);
		if (event.getBodyB() instanceof Explodable)
			tryExplode(event.getBodyB(), event.getBodyA(), event);
	}

	// precondition: body instanceof Explodable
	// perhaps use setBitmask(group) instead of addBit(group)
	private void tryExplode(Body body, Body other, CollisionEvent event) {
		if (exploded.contains(body)) // don't explode anything twice
			return;
		exploded.add(body);
		Explodable e = (Explodable)body;
		if (other instanceof Weapon) {
			stats.hit++;
			stats.dmg += getDamage(event, body);
		}
		// don't explode some offscreen or non-exploding bodies
		if (!isStuck(body, other) && (!e.canExplode()
				|| !display.inView(body.getPosition(), e.getRadius()+COLLIDE_BOUNDS)
				&& !(body instanceof Weapon))) {
			return;
		}
		world.remove(body);
		if (other instanceof Weapon)
			stats.kills++;
		Body rem = e.getRemnant();
		if (!(rem instanceof Explosion) && world.getBodies().size() > MAX_BODIES)
			return;
		List<Body> f = e.getFragments();
		long group = grouper.findGroup(body);
		if (rem instanceof Explosion) // gah, another special case
			explosionQueue.add((Explosion)rem);
		if (rem != null)
			rem.addBit(group);
		if (DOUBLE_GROUP && !(other instanceof Ship || body instanceof Weapon))
			other.addBit(group);
		// user-related stuff automatically passes the group limit
		if (!(body instanceof Ship || other instanceof Ship
				|| other instanceof Weapon || rem instanceof Explosion)) {
			body.addBit(group);
		}
		// not really J
		float J = other.getMass() / body.getMass() *
				   (body.getRestitution() + other.getRestitution()) / 2;
		J = Math.min(MAX_J, J);
		Vector2f v = sub(body.getVelocity(),(scale(other.getVelocity(),-J)));
		if (f != null) {
			for (Body b : f) {
				b.addBit(group);
				double theta = Math.random()*2*Math.PI;
				float sx = body.getPosition().getX();
				float sy = body.getPosition().getY();
				sx += e.getRadius() * (float)Math.sin(theta) / 2;
				sy -= e.getRadius() * (float)Math.cos(theta) / 2;
				sx += range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				sy -= range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				b.setRotation((float)(2 * Math.PI * Math.random()));
				b.adjustAngularVelocity(range(
				   -body.getAngularVelocity(), body.getAngularVelocity()));
				b.adjustVelocity(scale(direction(theta),
					(float)Math.sqrt(Math.random()*2*v.length())));
				if (!(body instanceof Ship)) // looks bad onscreen
					b.adjustVelocity(v);
				b.setPosition(sx, sy);
				world.add(b);
			}
		}
		if (rem != null) {
			if (rem instanceof Explosion && !(body instanceof Ship))
				rem.setPosition(event.getPoint().getX(),
					event.getPoint().getY());
			else
				rem.setPosition(body.getPosition().getX(),
					body.getPosition().getY());
			rem.adjustVelocity(v);
			rem.setRotation(body.getRotation());
			rem.adjustAngularVelocity(body.getAngularVelocity());
			world.add(rem);
		}
	}

	public static double getDamage(CollisionEvent e, Body victim) {
		Body other = e.getBodyA() == victim ? e.getBodyB() : e.getBodyA();
		if (other instanceof Weapon)
			return ((Weapon)other).getDamage();
		else if (other instanceof PowerUp) // got killed this way before :(
			return 0;
		double vmod = sub(victim.getVelocity(),other.getVelocity()).lengthSquared();
		return Math.min(other.getMass(),victim.getMass()) * vmod / 1e7;
	}

	// precondition: body instanceof Visible
	// only works for roughly circular asteroids (not ships, for example)
	public boolean isStuck(Body body, Body other) {
		if (!(body instanceof Asteroid && other instanceof Asteroid))
			return false; // too risky to evaluate
		Visible e = (Visible)body;
		float diff = sub(body.getPosition(),other.getPosition()).length();
		if (diff < e.getRadius() / 2)
			return true;
		return false;
	}
}
