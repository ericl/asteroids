package asteroids.test;
import asteroids.display.*;
import asteroids.bodies.*;
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RotateTest {
	protected JFrame frame;
	protected Display d;
	protected int width, height;

	public static void main(String[] args) {
		RotateTest demo = new RotateTest();
	}

	public RotateTest() {
		frame = new JFrame("Rotate Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		width = 500;
		height = 500;
		frame.setSize(width, height);
		
		int x = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getWidth()-width)/2;
		int y = (int)(Toolkit.getDefaultToolkit().
			getScreenSize().getHeight()-width)/2;
		
		frame.setLocation(x,y);

		Rock1 r = new Rock1(range(30,60));
		Sphere1 s = new Sphere1(range(31,41));
		d = new BasicDisplay(frame);
		d.setCenter(new Vector2f(160,250));
		float rot = 0;
		float xx = 50, yy = 50;
		float x2 = 300, y2 = 300;
		while (true) {
			r.setPosition(--x2,y2);
			s.setPosition(x2/2,y2/2);
			r.setRotation(rot);
			s.setRotation(rot);
			rot += .2;
			d.drawDrawable(r);
			d.drawTextured(r);
			d.drawDrawable(s);
			d.drawTextured(s);
			d.show();
			try {
				Thread.sleep(35);
			} catch (Exception e) {}
		}
	}
}
