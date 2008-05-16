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
			tryExplode(event.getBodyA(), event);
		if (event.getBodyB() instanceof Explodable)
			tryExplode(event.getBodyB(), event);
	}

	private void tryExplode(Body body, CollisionEvent event) {
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
		double theta = Math.random()*2*Math.PI;
		double tstep = 2*Math.PI / f.size();
		for (Body b : f) {
			b.setRotation((float)(2 * Math.PI * Math.random()));
			b.adjustVelocity(v(10*Math.sin(theta), 10*Math.cos(theta)));
			b.setPosition(body.getPosition().getX() + 30*(float)Math.sin(theta),
			              body.getPosition().getY() + 30*(float)Math.cos(theta));
			theta += tstep + Math.random() - Math.random();
		}
		for (Body b : f)
			world.add(b);
	}

	public static boolean worthyCollision(CollisionEvent e) {
		return Math.abs(e.getPenetrationDepth()) > 1;
	}
}
