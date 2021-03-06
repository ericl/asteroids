package asteroids.bodies;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.List;

import asteroids.weapons.Laser3;
import asteroids.weapons.ShieldFailing;

import net.phys2d.math.*;

import net.phys2d.raw.*;

import net.phys2d.raw.shapes.*;

import static asteroids.Util.*;

public class Terror extends AbstractEntity {
	protected static ROVector2f[] raw = {v(21,11),v(43,11),v(56,26),v(58,46),v(32,58),v(5,46),v(5,26)};

	public Terror(World world) {
		super(raw, null, 64, 64, 7500, world, null);
		setColor(Color.BLUE);
		setWeaponType(new Laser3(world, this));
		raiseShield = false;
	}

	public String getCause() {
		return "a blue terror";
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Polygon poly = (Polygon)getShape();
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.setColor(isVisible() ? Color.BLUE : Color.BLACK);
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	protected float getMaxArmor() {
		return 10;
	}

	public Body getRemnant() {
		return explosion = new ShieldFailing(this, getRadius());
	}

	public int getPointValue() {
		return 200;
	}

	public void setAccel(float accel) {
		this.accel = accel / 3;
	}

	public void modifyTorque(float t) {
		torque = t / 2;
	}

	public boolean targetableBy(Object o) {
		return super.targetableBy(o) && !(o instanceof Terror);
	}

	public List<Body> getFragments() {
		double min = Math.sqrt(getMass())/10;
		double max = Math.sqrt(getMass())/4;
		List<Body> f = new ArrayList<Body>(12);
		for (int i=0; i < 11; i++)
			f.add(new SpaceDebris(range(min,max)));
		for (Body b : f)
			((PolyBody)b).setColor(Color.BLUE);
		if (oneIn((int)(30/Math.sqrt(getPointValue()))))
			f.add(PowerUp.random());
		return f;
	}

	public boolean preferDrawableFallback() {
		return true;
	}
}
