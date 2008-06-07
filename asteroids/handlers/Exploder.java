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
	private World world;
	private Display display;
	private Stats stats;
	private CollisionGrouper grouper;

	static boolean DOUBLE_GROUP = true;
	static int MAX_BODIES = 300;
	static float MAX_RADIAL_DEVIATION = 10;
	static float COLLIDE_BOUNDS = 150;

	public Exploder(World w, Display d, Stats s) {
		world = w;
		display = d;
		stats = s;
		grouper = new CollisionGrouper(w);
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
	// so many special cases!
	private void tryExplode(Body body, Body other, CollisionEvent event) {
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
			rem.setBitmask(group);
		if (DOUBLE_GROUP && !(other instanceof Ship || body instanceof Weapon))
			other.setBitmask(group);
		// user-related stuff automatically passes the group limit
		if (!(body instanceof Ship || other instanceof Ship
				|| other instanceof Weapon || rem instanceof Explosion)) {
			body.setBitmask(group);
			if (!grouper.shouldFragment(body))
				return;
		}
		float J = other.getMass() / body.getMass() *
				   (body.getRestitution() + other.getRestitution()) / 2;
		Vector2f v = sub(body.getVelocity(),(scale(other.getVelocity(),-J)));
		if (f != null) {
			for (Body b : f) {
				b.setBitmask(group);
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

/**
 * phys2d will not collide bodies with any matching bits...
 * we use this to stop runaway explosions...
 * there are 63 bits/groups available for use so they wrap around
 */
class CollisionGrouper {
	private long nextmask = 1l;
	private int[] groups = new int[64];
	private static int MAX_GROUP_SIZE = 40;
	private World world;

	public CollisionGrouper(World w) {
		world = w;
	}

	/**
	 * update the group numbers, very fast operation
	 */
	private void recount() {
		int[] tmp = new int[65];
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b != null) // check for concurrent modification
				tmp[getBitIndex(b.getBitmask())]++;
		}
		groups = tmp;
	}

	/**
	 * @return index of first bit found on, 0 <= index <= 64
	 */
	private int getBitIndex(long in) {
		int j;
		// backwards to make 0x0 get index 0
		for (j = 64; j > 0; j--)
			if ((in & 1l << j) != 0)
				break;
		return j;
	}

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

	/**
	 * @return whether the body should fragment
	 */
	public boolean shouldFragment(Body b) {
		recount();
		if (getBitIndex(b.getBitmask()) == 0)
			return true;
		return groups[getBitIndex(b.getBitmask())] < MAX_GROUP_SIZE;
	}
}
