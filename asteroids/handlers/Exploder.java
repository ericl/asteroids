package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
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
		public Map<Long, CollisionGroup> grabMap(long gid) {
			// rather than figure out what needs to be discarded
			// it's easier to drop the whole map after inactivity
			if (System.currentTimeMillis() - mapTime > 5000)
				tmpMap = null;
			mapTime = System.currentTimeMillis();
			if (tmpMap == null)
				tmpMap = new HashMap<Long,CollisionGroup>(1000);
			if (!tmpMap.containsKey(gid))
				tmpMap.put(gid, new CollisionGroup(world,display,gid));
			return tmpMap;
		}
	}

	/**
	 * In the case of two large asteroids colliding head on:
	 * True - one of the two will explode and pass right through the other
	 * False - the physics engine will hang for a while on a large explosion
	 */
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
			tryExplode(event.getBodyA(), event.getBodyB(), event);
		if (event.getBodyB() instanceof Explodable)
			tryExplode(event.getBodyB(), event.getBodyA(), event);
	}

	private void tryExplode(Body body, Body other, CollisionEvent event) {
		Explodable e = (Explodable)body;
		e.collided(event);
		if (!e.canExplode()
				|| !display.inView(body.getPosition(), e.getRadius())
				&& event.getPenetrationDepth() < MIN_STUCK_DEPTH)
			return;
		world.remove(body);
		if (world.getBodies().size() > HARD_WORLD_LIMIT)
			return;
		long gid = e.getGID();
		List<Body> f = e.getFragments();
		Body rem = e.getRemnant();
		Map<Long,CollisionGroup> map = cmap.grabMap(gid);
		CollisionGroup group = map.get(gid);
		group.add(rem);
		if (DOUBLE_GROUP)
			group.add(other);
		float J = Math.min(other.getMass() / body.getMass() *
				   (body.getRestitution() + other.getRestitution()) / 2,
				   MAX_MOMENTUM_MULTIPLIER);
		Vector2f v = MathUtil.sub(body.getVelocity(),
			(MathUtil.scale(other.getVelocity(), -J)));
		if (!group.canExplode(e))
			return;
		group.remove(body);
		if (f != null) {
			float sx, sy, radius;
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
				b.adjustVelocity(MathUtil.scale(direction(theta), RADIAL_VELOCITY));
				b.adjustVelocity(v);
				b.setPosition(sx, sy);
				theta += tstep + range(-MAX_ANGLE_DEVIATION,MAX_ANGLE_DEVIATION);
				world.add(b);
			}
		}
		
		if (rem != null) {
			rem.setPosition(body.getPosition().getX(), body.getPosition().getY());
			rem.adjustVelocity(v);
			rem.setRotation(body.getRotation());
			rem.adjustAngularVelocity(body.getAngularVelocity());
			world.add(rem);
		}
	}

	public static boolean worthyCollision(CollisionEvent e) {
		return Math.abs(e.getPenetrationDepth()) > ASTEROID_DURABILITY;
	}
}


class CollisionGroup {
	// queue of this collision's smallest fragments
	private PriorityQueue<Asteroid> prio = new PriorityQueue<Asteroid>();
	// temporary queue to avoid looking through prio too much
	private Queue<Asteroid> onscreen = new LinkedList<Asteroid>();
	// all the fragments of this collision group
	private Set<Asteroid> set = new HashSet<Asteroid>();
	private long gid, lastTime = 0;
	private World world;
	private Display display;

	static int SOFT_GROUP_LIMIT = 35;
	static int HARD_GROUP_LIMIT = 50;
	static int SOFT_WORLD_LIMIT = 175;

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
				for (Body y : set)
					x.addExcludedBody(y);
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
		if (System.currentTimeMillis() - lastTime > 1000) {
			while (!onscreen.isEmpty())
				prio.add(onscreen.remove());
			lastTime = System.currentTimeMillis();
		}
		if (!set.contains(e) || set.size() < SOFT_GROUP_LIMIT
				&& world.getBodies().size() < SOFT_WORLD_LIMIT)
			return true;
		int num = 10*(set.size() - SOFT_GROUP_LIMIT) /
		          (HARD_GROUP_LIMIT - SOFT_GROUP_LIMIT);
		Asteroid c;
		for (int i=0; i < num && !prio.isEmpty(); i++) {
			c = prio.poll();
			if (!display.inView(c.getPosition(), c.getRadius())) {
				world.remove(c);
				remove(c);
			} else
				onscreen.add(c);
		}
		return set.size() < HARD_GROUP_LIMIT;
	}
}
