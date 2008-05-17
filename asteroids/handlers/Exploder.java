package asteroids.handlers;
import asteroids.bodies.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import java.util.*;

public class Exploder implements CollisionListener {
	private World world;

	public Exploder(World w) {
		world = w;
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
		if (!e.canExplode())
			return;
		List<Body> f = e.explode();
		world.remove(body);
		if (world.getBodies().size() > 150 || f == null || f.size() < 1)
			return;
		for (Body j : f)
			for (Body k : f)
				if (j != k)
					j.addExcludedBody(k);
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		// TODO: restitution and true conservation of momentum
		float mf = Math.min(other.getMass() / body.getMass() * .5f, 3);
		float vx = body.getVelocity().getX() + mf * other.getVelocity().getX();
		float vy = body.getVelocity().getY() + mf * other.getVelocity().getY();
		float sx, sy;
		double theta = Math.random()*2*Math.PI;
		double tstep = 2*Math.PI / f.size();
		for (Body b : f) {
			sx = body.getPosition().getX() + 20*(float)Math.sin(theta);
			sy = body.getPosition().getY() + 20*(float)Math.cos(theta);
			sx += (float)(20*Math.random());
			sy += (float)(20*Math.random());
			b.setRotation((float)(2 * Math.PI * Math.random()));
			b.adjustVelocity(v(vx+10*Math.sin(theta), vy+10*Math.cos(theta)));
			b.setPosition(sx, sy);
			theta += tstep + Math.random() - Math.random();
		}
		for (Body b : f)
			world.add(b);
	}

	public static boolean worthyCollision(CollisionEvent e) {
		return Math.abs(e.getPenetrationDepth()) > .1;
	}
}
