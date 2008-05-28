/**
 * [make this work well]
 */

package asteroids.test;
import asteroids.handlers.*;
import asteroids.handlers.Timer;
import asteroids.display.*;
import asteroids.bodies.*;
import static asteroids.Util.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import javax.swing.JFrame;
import java.awt.*;

public class OverlapTest {
	protected JFrame frame;
	protected Display d;
	protected int width, height;

	public static void main(String[] args) {
		new OverlapTest();
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
		d = new BasicDisplay(frame, new Dimension(width, height));
		d.setCenter(new Vector2f(250,250));
		World world = new World(v(0,0), 10, new QuadSpaceStrategy(20,5));

		Body s = new HexAsteroid(40);
		Body t = new CircleAsteroid(40);
		Body a = new HexAsteroid(30);
		Body b = new CircleAsteroid(30);
		Body f = new CircleAsteroid(30);
		Body g = new CircleAsteroid(30);
		Body z = new CircleAsteroid(130);
		Body q1 = new CircleAsteroid(130);
		Body q2 = new CircleAsteroid(130);
		world.enableRestingBodyDetection(.1f, .1f, .1f);
		world.add(s);
		world.add(t);
		world.add(a);
		world.add(b);
		world.add(f);
		world.add(g);
		world.add(z);
		world.add(q1);
		world.add(q2);
		z.adjustVelocity(v(80,-20));
		world.addListener(new Exploder(world, d));
		s.setPosition(350,150);
		t.setPosition(350,150);
		a.setPosition(420,250);
		b.setPosition(430,250);
		f.setPosition(150,200);
		g.setPosition(150,400);
		z.setPosition(150,300);
		q1.setPosition(300,-180);
		q2.setPosition(300,-120);
		d.show();
		Timer timer = new Timer(60f);
		while (true) {
			d.drawWorld(world);
			d.show();
			world.step(timer.tick());
		}
	}
}
