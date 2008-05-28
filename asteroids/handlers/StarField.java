package asteroids.handlers;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.strategies.*;
import asteroids.display.*;
import asteroids.bodies.*;
import asteroids.handlers.*;
import java.util.*;
import static asteroids.Util.*;

public class StarField {
	protected int border = 300, buf = 500;
	protected LinkedList<Vector2f> stars = new LinkedList<Vector2f>();
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	protected final Ship ship;
	private Display current;
	
	public StarField(Ship s, Display o) {
		ship = s;
		current = o;
	}
	
	public void starField() {
		Graphics2D graphics = current.getGraphics();
		ListIterator<Vector2f> iter = stars.listIterator();
		int numStars = 0;
		graphics.setColor(Color.gray);
		while (iter.hasNext()) {
			Vector2f star = iter.next();
			if (isVisible(ship.getPosition(), v(500,500), star, border+buf))
				numStars++;
			else
				iter.remove();
		}
		if (numStars < 500)
			stars.add(getOffscreenCoords(10f));
		for (Vector2f star : stars) {
			Vector2f tmp = MathUtil.sub(star, ship.getPosition());
			int radius = (int)range(2,4);
			int disp = (int)(radius/2);
			graphics.fillOval((int)tmp.getX()-disp, (int)tmp.getY()-disp, radius, radius);
		}
	}

	protected static boolean isVisible(ROVector2f o , ROVector2f dim, ROVector2f v, float r) {
		Vector2f rel = MathUtil.sub(v,o);
		return rel.getX() > -r && rel.getX() < dim.getX()+r && rel.getY() < dim.getY()+r;
	}
	protected boolean onScreen(ROVector2f v, float r) {
		float w2 = (WIDTH/2 + r);
		float h2 = (HEIGHT/2 + r);
		float x = v.getX();
		float y = v.getY();
		return x > -w2-r && x < w2 && y > -h2-r && y < h2;
	}
	
	protected Vector2f getOffscreenCoords(float r) {
		float x = 1, y = 1;
		// this is centered about the screen origin
		float xo = ship.getPosition().getX();
		float yo = ship.getPosition().getY();
		while (onScreen(v(x,y),r)) {
			x = (float)(Math.random()*2*(WIDTH + border) - WIDTH - border);
			y = (float)(Math.random()*2*(HEIGHT + border) - HEIGHT - border);
		}
		return v(x+xo+WIDTH/2+r,y+yo+HEIGHT/2+r);
	}
}
