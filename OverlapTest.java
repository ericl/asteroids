/**
 * All the bad situations here need to be solved.
 * 1. There are two vibrating overlapping shapes.
 *    They should not be doing this.
 * 2. An object is created in midst of others, pushing them away.
 *    There should be some way to avoid this.
 * Note that solving (2) will probably solve (1).
 */

import asteroids.*;
import asteroids.display.*;
import asteroids.bodies.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import javax.swing.JFrame;
import java.awt.Frame;
import java.awt.Toolkit;

public class OverlapTest {
	protected JFrame frame;
	protected Display d;
	protected int width, height;

	public static void main(String[] args) {
		OverlapTest demo = new OverlapTest();
	}

	public OverlapTest() {
		frame = new JFrame("Overlap Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		width = 500;
		height = 500;
		frame.setSize(width, height);
		
		int x = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getWidth()-width)/2;
		int y = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getHeight()-width)/2;
		
		frame.setLocation(x,y);
		d = new Display(frame);
		d.setCenter(new Vector2f(0,0));
		final World world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));

		Body s = new BoxAsteroid(40);
		Body t = new CircleAsteroid(40);
		Body a = new BoxAsteroid(30);
		Body b = new CircleAsteroid(30);
		Body f = new CircleAsteroid(30);
		Body g = new CircleAsteroid(30);
		Body z = new CircleAsteroid(130);
		world.add(s);
		world.add(t);
		world.add(a);
		world.add(b);
		world.add(f);
		world.add(g);
		world.add(z);
		world.addListener(new CollisionListener() {
			public void collisionOccured(CollisionEvent event) {
				if (event.getBodyA() instanceof Explodable) {
					Explodable A = (Explodable)event.getBodyA();
					if (A.canExplode())
						for (Body b : A.explode())
							world.add(b);
					world.remove(event.getBodyA());
				}
				if (event.getBodyB() instanceof Explodable) {
					Explodable B = (Explodable)event.getBodyB();
					if (B.canExplode())
						for (Body b : B.explode())
							world.add(b);
					world.remove(event.getBodyB());
				}
			}
		});
		s.setPosition(350,150);
		t.setPosition(350,150);
		a.setPosition(420,250);
		b.setPosition(430,250);
		f.setPosition(150,200);
		g.setPosition(150,400);
		z.setPosition(150,300);
		d.clearBuffer();
		try {
			Thread.sleep(100);
		} catch (Exception e) {}
		while (true) {
			d.drawWorld(world);
			d.show();
			world.step();
			try {
				Thread.sleep(35);
			} catch (Exception e) {}
		}
	}
}
