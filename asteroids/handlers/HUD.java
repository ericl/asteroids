package asteroids.handlers;
import asteroids.bodies.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.awt.*;
import asteroids.display.*;

public class HUD {
	private static int DIM = 50;
	private Display display;
	private World world;
	private Ship target;

	public HUD(Display d, World w, Ship t) {
		target = t;
		display = d;
		world = w;
	}

	/**
	 * Updates position and draws to the screen.
	 */
	public void drawTo(Graphics2D g2d) {
		BodyList bodies = world.getBodies();
		int r = 1;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(display.w(0)/2-DIM, display.h(-2*DIM), 2*DIM, 2*DIM);
		for (int i=0; i < bodies.size(); i++) {
			if (bodies.get(i) instanceof Ship) {
				g2d.setColor(Color.RED);
				r = 4;
			} else {
				g2d.setColor(Color.BLACK);
				r = 1;
			}
			if (bodies.get(i) == target) {
				g2d.setColor(Color.BLACK);
				r = 4;
			}
			ROVector2f dest = bodies.get(i).getPosition();	
			Vector2f delta = MathUtil.sub(target.getPosition(), dest);
			int x1 = display.w(0)/2;
			int y1 = display.h(0)-DIM;
			int sx = (int)sign(delta.getX());
			int sy = (int)sign(delta.getY());
			int x2 = (int)(3*sx*Math.sqrt(Math.abs(delta.getX())));
			int y2 = (int)(3*sy*Math.sqrt(Math.abs(delta.getY())));
			if (Math.abs(x2) > DIM)
				continue;
			if (Math.abs(y2) > DIM)
				continue;
			g2d.fillOval(x1-x2-r/2, y1-y2-r/2, r, r);
		}
	}
}
