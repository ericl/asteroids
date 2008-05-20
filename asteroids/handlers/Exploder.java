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

	// I'm fairly sure this doesn't need to be synchronized
	private void tryExplode(Body body, Body other, CollisionEvent event) {
		Explodable e = (Explodable)body;
		e.collided(event);
		/*
		 * Don't destroy bodies outside the field of vision:
		 *   1. the game is too easy with tiny bits floating everywhere
		 *   2. it causes physics slowdowns with no visible reason
		 * High energy collisions are allowed to avoid "stuck" bodies.
		 */
		if (!e.canExplode() ||
				body instanceof Visible &&
				!display.isVisible(body.getPosition(),((Visible)body).getRadius()) &&
				event.getPenetrationDepth() < 5)
			return;
		List<Body> f = e.explode();
		world.remove(body);
		if (world.getBodies().size() > 100 || f == null || f.size() < 1)
			return;
		for (Body j : f)
			for (Body k : f)
				if (j != k)
					j.addExcludedBody(k);
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		float res = (body.getRestitution() + other.getRestitution())/2;
		float mf = Math.min(other.getMass() / body.getMass() * res, 3);
		float vx = body.getVelocity().getX() + mf * other.getVelocity().getX();
		float vy = -body.getVelocity().getY() + mf * other.getVelocity().getY();
		float sx, sy;
		double theta = Math.random()*2*Math.PI;
		double tstep = 2*Math.PI / f.size();
		for (Body b : f) {
			sx = body.getPosition().getX();
			sy = body.getPosition().getY();
			if (b instanceof Visible) {
				sx += ((Visible)b).getRadius()*(float)Math.sin(theta);
				sy -= ((Visible)b).getRadius()*(float)Math.cos(theta);
			} else {
				sx += 20*(float)Math.sin(theta);
				sy -= 20*(float)Math.cos(theta);
			}
			sx += (float)(20*Math.random()) - 10;
			sy -= (float)(20*Math.random()) - 10;
			b.setRotation((float)(2 * Math.PI * Math.random()));
			b.adjustAngularVelocity((float)Math.random()
			    * body.getAngularVelocity());
			b.adjustVelocity(MathUtil.scale(direction(theta),10));
			b.adjustVelocity(v(vx,vy));
			b.setPosition(sx, sy);
			theta += tstep + Math.random() - .5;
		}
		for (Body b : f)
			world.add(b);
	}

	public static boolean worthyCollision(CollisionEvent e) {
		return Math.abs(e.getPenetrationDepth()) > .1;
	}
}
