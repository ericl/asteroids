package asteroids.bodies;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Ship extends Body implements Drawable, Textured, Explodable, KeyListener {
	
	private static ROVector2f[] poly = {v(-1,-28), v(3,-24), v(5,-16), v(6,-9), v(5,-3), v(8,-4), v(23,-4), v(23,0), v(9,8), v(5,4), v(6,13), v(-8,13), v(-8,4), v(-10,8), v(-24,1), v(-24,-4), v(-9,-4), v(-5,-2), v(-6,-8), v(-6,-16), v(-4,-25)};
	private static Shape shape = new Polygon(poly);
	private double hull = 1;
	private int thrust;
	private float accel, torque;
	private boolean fire, explode;
	private long lastFired;
	private World world;

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

	public void collided(CollisionEvent event) {
		hull -= Math.pow(MathUtil.sub(event.getBodyA().getVelocity(), event.getBodyB().getVelocity()).length() / 100, 2);
		explode = hull < 0;
	}

	public boolean canExplode() {
		return explode;
	}

	public float getTextureScaleFactor() {
		return 1.0f;
	}

	public List<Body> explode() {
		HexAsteroid hull = new HexAsteroid(21);
		hull.setColor(Color.DARK_GRAY);
		// not realistic, but looks better onscreen
		adjustVelocity(MathUtil.sub(v(0,0),getVelocity()));
		return hull.explode();
	}

	public String getTexturePath() {
		return thrust > 0 ? "pixmaps/ship-t.png" : "pixmaps/ship.png";
	}

	public Vector2f getTextureCenter() {
		return v(33,32);
	}

	public double getDamage() {
		return hull < 0 ? 0 : hull;
	}

	public void drawTo(Graphics2D g2d, float xo, float yo) {
		Polygon poly = (Polygon)getShape();
		g2d.setColor(Color.black);
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - xo);
			ycoords[i] = (int)(verts[i].getY() - yo);
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
			case KeyEvent.VK_SPACE: fire = true; break;
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

	// be careful not to use methods that do not account for varying dt!
	public void endFrame() {
		thrust--;
		accel();
		torque();
		fire();
	}

	private void accel() {
		if (accel > 0)
			thrust = 5;
		Vector2f dir = direction(getRotation());
		addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
	}

	private void torque() {
		// unfortunately setTorque() gives unpredictable results with changing dt
		adjustAngularVelocity(getMass()*torque);
	}

	private void fire() {
		long timenow = System.currentTimeMillis();
		if (canExplode() || !fire || timenow - lastFired < 150)
			return;
		lastFired = timenow;
		Body c = new Sphere1(3, 70f);
		c.setRotation(getRotation());
		float ax = (float)(20*Math.sin(getRotation()));
		float ay = (float)(20*Math.cos(getRotation()));
		c.setPosition(getPosition().getX()+ax, getPosition().getY()-ay);
		c.adjustVelocity(v(20*ax,20*-ay));
		c.addExcludedBody(this);
		world.add(c);
	}

	public void keyTyped(KeyEvent e) {
		// don't care
	}
}
