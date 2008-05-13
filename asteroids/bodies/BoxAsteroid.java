package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class BoxAsteroid extends Asteroid implements Drawable {
	private float radius;

	public BoxAsteroid(float length) {
		super(new Box(length, length), length*length);
		this.radius = (float)((length / 2) * Math.sqrt(2));
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
		return radius;
	}
}
