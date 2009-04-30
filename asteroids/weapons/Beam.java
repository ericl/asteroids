/**
 * Powerful, fast-moving blast.
 */

package asteroids.weapons;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.*;

import asteroids.display.*;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

import static asteroids.Util.*;

public class Beam extends Weapon implements Drawable, Heavy {
	private static ROVector2f[] geo = {v(-1,5),v(1,5),v(1,-5),v(-1,-5)};
	private static ROVector2f[] geo2 = {v(-.3,5),v(.3,5),v(.3,-5),v(-.3,-5)};
	private static Polygon poly = new Polygon(geo);
	private static Polygon poly2 = new Polygon(geo2);
	private static float myRadius = 3;
	private boolean explode;
	private SharedBeam state;

	public Beam() {
		super(new Circle(myRadius), 60);
		state = new SharedBeam();
		setRestitution(1);
	}

	public Beam(SharedBeam state) {
		super(new Circle(myRadius), 60);
		this.state = state;
		setRestitution(1);
	}

	protected long getLifetime() {
		return 1000;
	}

	public boolean isMaxed() {
		return true;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.setColor(new Color(95, 175, 221));
		g2d.fillPolygon(xcoords, ycoords, verts.length);
		verts = poly2.getVertices(getPosition(), getRotation());
		xcoords = new int[verts.length];
		ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.setColor(Color.WHITE);
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	public boolean preferDrawableFallback() {
		return true;
	}

	public Beam duplicate() {
		if (!state.continuous())
			state = new SharedBeam();
		return new Beam(state);
	}

	public boolean hasPreferredRotation() {
		return state.getRotation() != null;
	}

	public float getPreferredRotation() {
		return state.getRotation();
	}

	public boolean hasPreferredVelocity() {
		return state.getVelocity() != null;
	}

	public Vector2f getPreferredVelocity() {
		return state.getVelocity();
	}

	public void hintVelocity(Vector2f vel) {
		if (state.getVelocity() == null)
			state.setVelocity(vel);
		adjustVelocity(vel);
	}

	public void setRotation(float rotation) {
		if (state.getRotation() == null)
			state.setRotation(rotation);
		super.setRotation(rotation);
	}

	public Body getRemnant() {
		return new LaserExplosion(Explosion.TrackingMode.TARGET, .5f);
	}

	public List<Body> getFragments() {
		return null;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getDamage() {
		return .10f;
	}

	public Vector2f getTextureCenter() {
		return v(6.5,6.5);
	}

	public float getTextureScaleFactor() {
		return 1f;
	}

	public String getTexturePath() {
		return "pixmaps/cannon.png";
	}

	public float getLaunchSpeed() {
		return 50;
	}

	public float getWeaponSpeed() {
		return 50;
	}

	public float getRadius() {
		return myRadius;
	}

	public void collided(CollisionEvent e) {
		Body other = e.getBodyA() == this ? e.getBodyB() : e.getBodyA();
		explode = !(other instanceof Weapon) || other instanceof Heavy;
	}

	public int getNum() {
		return 1;
	}

	public float getReloadTime() {
		return 0;
	}
}

class SharedBeam {
	private Float rotation;
	private Vector2f velocity;
	private long time;

	public SharedBeam() {
		this.time = System.currentTimeMillis();
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public Float getRotation() {
		return rotation;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public boolean continuous() {
		boolean ret = true;
		long t = System.currentTimeMillis();
		if (t > time + 100)
			ret = false;
		time = t;
		return ret;
	}
}
