package asteroids.bodies;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import asteroids.display.*;
import asteroids.handlers.Exploder;
import asteroids.weapons.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class Ship extends Body implements Drawable, Textured, Explodable, KeyListener {
	protected static ROVector2f[] poly = {v(-1,-28), v(3,-24), v(5,-16), v(6,-9), v(5,-3), v(8,-4), v(23,-4), v(23,0), v(9,8), v(5,4), v(6,13), v(-8,13), v(-8,4), v(-10,8), v(-24,1), v(-24,-4), v(-9,-4), v(-5,-2), v(-6,-8), v(-6,-16), v(-4,-25)};
	protected static Shape shape = new Polygon(poly);
	protected double hull = 1;
	protected int thrust;
	protected float accel, torque;
	protected boolean fire, explode;
	protected long lastFired, gid = -1;
	protected World world;
	protected boolean invincible;
	public int deaths;
	protected Laser laser = new Laser();
	protected WeaponsSys lasersys = new WeaponsSys(laser);

	public void reset() {
		setRotation(0);
		setPosition(0,0);
		adjustVelocity(MathUtil.sub(v(0,0),getVelocity()));
		adjustAngularVelocity(-getAngularVelocity());
		accel = torque = lastFired = 0;
		fire = explode = false;
		hull = 1;
		thrust = 0;
	}

	public Ship(World w) {
		super("Your ship", shape, 1000f);
		world = w;
		setRotDamping(4000);
	}

	public void setInvincible(boolean b) {
		invincible = b;		
	}

	public void collided(CollisionEvent event) {
		hull -= Exploder.getDamage(event, this);
		explode = hull < 0;
	}

	public boolean canExplode() {
		return explode && !invincible;
	}

	public float getTextureScaleFactor() {
		return 1.0f;
	}

	public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		HexAsteroid hull = new HexAsteroid(21);
		hull.setColor(Color.DARK_GRAY);
		// not realistic, but looks better onscreen
		adjustVelocity(MathUtil.sub(v(0,0),getVelocity()));
		return hull.getFragments();
	}

	public String getTexturePath() {
		return thrust > 0 ? "pixmaps/ship-t.png" : "pixmaps/ship.png";
	}

	public Vector2f getTextureCenter() {
		return v(33,32);
	}

	public double getDamage() {
		return invincible ? Double.POSITIVE_INFINITY : hull < 0 ? 0 : hull;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Polygon poly = (Polygon)getShape();
		g2d.setColor(Color.black);
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	public float getRadius() {
		return 45;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT: torque = -.00008f; break;
			case KeyEvent.VK_RIGHT: torque = .00008f; break;
			case KeyEvent.VK_UP: accel = 10; break;
			case KeyEvent.VK_DOWN: accel = -5; break;
			case KeyEvent.VK_SPACE: fire(); break;
// fire = true;
// fire();
// break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: torque = 0; break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN: accel = 0; break;
			case KeyEvent.VK_SPACE: fire = false; break;
		}
	}

	public void endFrame() {
		super.endFrame();
		float v = getVelocity().length();
		setDamping(v < 40 ? 0 : v < 100 ? .4f : 1f);
		thrust--;
		accel();
		torque();
		// fire();
	}

	protected void accel() {
		if (accel > 0)
			thrust = 5;
		Vector2f dir = direction(getRotation());
		addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
	}

	protected void torque() {
		// setTorque() is unpredictable with varied dt
		adjustAngularVelocity(getMass()*torque);
	}

	protected void fire() {
		if(canExplode()) return;
		lasersys.fire(this, world);
	}

	public void keyTyped(KeyEvent e) {
		// don't care
	}

	public long getGID() {
		if (gid < 0)
			gid = System.nanoTime();
		return gid;
	}

	public void setGID(long id) {
		gid = id;
	}
}
