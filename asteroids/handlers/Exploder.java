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
	
	// fragments move outwards with this velocity
	static float RADIAL_VELOCITY = 5;

	// collisions of this depth are allowed to occur offscreen
	static float MIN_STUCK_DEPTH = 5;

	// velocity imparted is at max this times the original
	static float MAX_MOMENTUM_MULTIPLIER = 3;

	// number of bodies in the world before explosions are suppressed
	static float MAX_BODIES = 100;

	// randomness of angles, in radians
	static float MAX_ANGLE_DEVIATION = .7f;

	// randomness of radius, in pixels
	static float MAX_RADIAL_DEVIATION = 10;

	// min collision depth to break up an asteroid
	static float ASTEROID_DURABILITY = .1f;

	public Exploder(World w, Display d) {
		world = w;
		display = d;
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
		List<Body> f = e.getFragments();
		Body rem = e.getRemnant();
		world.remove(body);
		if (world.getBodies().size() > MAX_BODIES)
			return;
		float J = Math.min(other.getMass() / body.getMass() *
				   (body.getRestitution() + other.getRestitution()) / 2,
				   MAX_MOMENTUM_MULTIPLIER);
		Vector2f v = MathUtil.sub(body.getVelocity(), (MathUtil.scale(other.getVelocity(), -J)));
		if (f != null) {
			for (Body j : f) {
				for (Body k : f) 
					if (j != k)
						j.addExcludedBody(k);
				if (rem != null) {
					rem.addExcludedBody(j);
					j.addExcludedBody(rem);
				}
			}
			float sx, sy, radius;
			double theta = Math.random()*2*Math.PI;
			double tstep = 2*Math.PI / f.size();
			for (Body b : f) {
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
}
