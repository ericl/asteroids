package asteroids.weapons;

import java.util.LinkedList;
import java.util.List;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import asteroids.bodies.*;

public class Laser extends Weapon {

	private float sphereradius;

	public Laser() {
		super((new Circle(2)),600,8f);
		sphereradius = 2;
	}

	public Vector2f getTextureCenter() {
		return v(7,3);
	}

	public boolean canExplode() {
		return true;
	}

	public Body getRemnant() {
		return null;
	}
	
	public List<Body> getFragments() {
		List<Body> f = new LinkedList<Body>();
		if (getRadius() > 10)
			for (int i=0; i < 4; i++)
				f.add(new Sphere1(getRadius() / 3));	
		return f;
	}

	public long getGID() {
		return 0;
	}
	
	public void setGID(long l) {
		return;
	}
	
	public String getTexturePath() {
		return "pixmaps/laser.png";
	}

	public float getTextureScaleFactor() {
		return sphereradius / 2;
	}
	
	public float getRadius() {
		return sphereradius;
	}

	public void fire(World w, Ship s) {
		if(canFire()) {
			Laser c = new Laser();
			c.setRotation(getRotation());
			float ax = (float)(20*Math.sin(s.getRotation()));
			float ay = (float)(20*Math.cos(s.getRotation()));
			c.setPosition(getPosition().getX()+ax, getPosition().getY()-ay);
			c.adjustVelocity(v(20*ax,20*-ay));
			c.addExcludedBody(s);
			w.add(c);
			System.out.println("FIRE");
		}
	}
	
	private static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}
}
