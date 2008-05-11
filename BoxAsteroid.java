import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class BoxAsteroid extends Asteroid implements Drawable {

	public BoxAsteroid(Box box) {
		super(box, (float)Math.pow(box.getSize().length(),2));
	}

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Box box = (Box)getShape();
		g2d.setColor(Color.black);
		ROVector2f[] verts = box.getPoints(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - xo);
			ycoords[i] = (int)(verts[i].getY() - yo);
		}
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	public float getRadius() {
		return ((Box)getShape()).getSize().length() / 2;
	}

	public static BoxAsteroid random(int minR, int maxR) {
		float l = (float)(minR+(maxR-minR)*Math.random());
		Box box = new Box(l,l);
		return new BoxAsteroid(box);
	}
}
