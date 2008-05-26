package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class Exploder implements CollisionListener {
	private World world;
	private Display display;
	private CollisionMap cmap;

	private class CollisionMap {
		private Map<Long,CollisionGroup> tmpMap;
		private long mapTime;
		private World world;
		private Display display;
		public CollisionMap(World w, Display d) {
			world = w;
			display = d;
		}
		public CollisionGroup get(long gid) {
			// rather than figure out what needs to be discarded
			// it's easier to drop the whole map after inactivity
			if (tmpMap == null || System.currentTimeMillis() - mapTime > 5000)
				tmpMap = new HashMap<Long,CollisionGroup>(1000);
			mapTime = System.currentTimeMillis();
			if (!tmpMap.containsKey(gid))
				tmpMap.put(gid, new CollisionGroup(world,display,gid));
			return tmpMap.get(gid);
		}
	}

	static boolean DOUBLE_GROUP = true;
	static int HARD_WORLD_LIMIT = 250;
	static float RADIAL_VELOCITY = 5;
	static float MIN_STUCK_DEPTH = 5;
	static float MAX_MOMENTUM_MULTIPLIER = 3;
	static float MAX_ANGLE_DEVIATION = .7f;
	static float MAX_RADIAL_DEVIATION = 10;
	static float ASTEROID_DURABILITY = .1f;

	public Exploder(World w, Display d) {
		world = w;
		display = d;
		cmap = new CollisionMap(world, display);
	}

	public void collisionOccured(CollisionEvent event) {
		if (event.getBodyA() instanceof Explodable)
			((Explodable)event.getBodyA()).collided(event);
		if (event.getBodyB() instanceof Explodable)
			((Explodable)event.getBodyB()).collided(event);
		if (event.getBodyA() instanceof Explodable)
			tryExplode(event.getBodyA(), event.getBodyB(), event);
		if (event.getBodyB() instanceof Explodable)
			tryExplode(event.getBodyB(), event.getBodyA(), event);
	}

	private void tryExplode(Body body, Body other, CollisionEvent event) {
		Explodable e = (Explodable)body;
		if (!e.canExplode()
				|| !display.inView(body.getPosition(), e.getRadius())
				&& event.getPenetrationDepth() < MIN_STUCK_DEPTH)
			return;
		world.remove(body);
		if (world.getBodies().size() > HARD_WORLD_LIMIT)
			return;

		long gid = e.getGID();
		List<Body> f = e.getFragments();
		CollisionGroup group = cmap.get(gid);
		Body rem = e.getRemnant();
		group.add(rem);
		if (DOUBLE_GROUP && other instanceof Explodable
				&& ((Explodable)other).canExplode())
			group.add(other);
		if (!(other instanceof Ship)
				&& !(other instanceof Sphere1)
				&& !(body instanceof Ship)
				&& !group.canExplode(e))
			return;
		group.remove(body);

		float J = Math.min(other.getMass() / body.getMass() *
				   (body.getRestitution() + other.getRestitution()) / 2,
				   MAX_MOMENTUM_MULTIPLIER);
		Vector2f v = sub(body.getVelocity(),(scale(other.getVelocity(),-J)));
		if (f != null) {
			float sx, sy;
			double theta = Math.random()*2*Math.PI;
			double tstep = 2*Math.PI / f.size();
			for (Body b : f) {
				group.add(b);
				sx = body.getPosition().getX();
				sy = body.getPosition().getY();
				sx += e.getRadius() * (float)Math.sin(theta) / 2;
				sy -= e.getRadius() * (float)Math.cos(theta) / 2;
				sx += range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				sy -= range(-MAX_RADIAL_DEVIATION, MAX_RADIAL_DEVIATION);
				b.setRotation((float)(2 * Math.PI * Math.random()));
				b.adjustAngularVelocity(range(
				   -body.getAngularVelocity(), body.getAngularVelocity()));
				b.adjustVelocity(scale(direction(theta), RADIAL_VELOCITY));
				b.adjustVelocity(v);
				b.setPosition(sx, sy);
				theta += tstep + range(-MAX_ANGLE_DEVIATION,MAX_ANGLE_DEVIATION);
				world.add(b);
			}
		}
		if (rem != null) {
			rem.setPosition(body.getPosition().getX(),
				body.getPosition().getY());
			rem.adjustVelocity(v);
			rem.setRotation(body.getRotation());
			rem.adjustAngularVelocity(body.getAngularVelocity());
			world.add(rem);
		}
	}

	public static boolean worthyCollision(CollisionEvent e) {
		return Math.abs(e.getPenetrationDepth()) > ASTEROID_DURABILITY;
	}

	public static double getDamage(CollisionEvent e, Body victim) {
		Body other = e.getBodyA() == victim ? e.getBodyB() : e.getBodyA();
		double vmult = sub(victim.getVelocity(),other.getVelocity()).lengthSquared();
		return Math.min(other.getMass(),1000) * vmult / 1e7;
	}
}

/**
 * The cpu used by this is tiny compared to that of the
 * display and physics engine. Therefore we may as well
 * try to make it look good.
 */
class CollisionGroup {
	// queue of this collision's smallest fragments
	private PriorityQueue<Asteroid> prio = new PriorityQueue<Asteroid>();
	// all the fragments of this collision group
	private Set<Asteroid> set = new HashSet<Asteroid>();
	private long gid;
	private World world;
	private Display display;

	static int LOWER_GROUP_LIMIT = 35;
	static int UPPER_GROUP_LIMIT = 50;
	static int LOWER_WORLD_LIMIT = 175;
	static int UPPER_WORLD_LIMIT = 200;
	static int MAX_OFFSCREEN_SEARCH = 15;

	public CollisionGroup(World world, Display display, long gid) {
		this.gid = gid;
		this.world = world;
		this.display = display;
	}

	public void add(Body b) {
		if (b instanceof Asteroid) {
			Asteroid c = ((Asteroid)b);
			c.setGID(gid);
			set.add(c);
			for (Body x : set)
				x.addExcludedBody(b);
			prio.add(c);
		}
	}

	public void remove(Body b) {
		set.remove(b);
		if (b instanceof Asteroid)
			prio.remove((Asteroid)b);
	}

	// all this processing still uses little cpu vs rendering
	public boolean canExplode(Explodable e) {
		if (!set.contains(e) || set.size() < LOWER_GROUP_LIMIT
				&& world.getBodies().size() < LOWER_WORLD_LIMIT)
			return true;

		int num = MAX_OFFSCREEN_SEARCH*(set.size() - LOWER_GROUP_LIMIT) /
		          (UPPER_GROUP_LIMIT - LOWER_GROUP_LIMIT);
		Asteroid c;
		Queue<Asteroid> onscreen = new LinkedList<Asteroid>();
		// look for small offscreen objects to remove
		for (int i=0; i < num && !prio.isEmpty(); i++) {
			c = prio.poll();
			if (!display.inView(c.getPosition(), c.getRadius())) {
				world.remove(c);
				set.remove(c);
			} else
				onscreen.add(c);
		}
		while (!onscreen.isEmpty())
			prio.add(onscreen.remove());
		return set.size() <= UPPER_GROUP_LIMIT + Math.sqrt(e.getRadius())
			&& world.getBodies().size()
			<= UPPER_WORLD_LIMIT + Math.sqrt(e.getRadius());
	}
}
