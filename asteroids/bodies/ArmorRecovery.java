package asteroids.bodies;

import static asteroids.Util.*;
import asteroids.display.*;
import asteroids.handlers.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class ArmorRecovery extends PowerUp implements Textured {
	
	private static ROVector2f[] raw = { v(176,8), v(325,30), v(311,54), v(307,78), v(310,113), v(310,138), v(306,167), v(281,212), v(176,329), v(56,186), v(44,159), v(41,140), v(41,115), v(44,80), v(40,53), v(28,28), v(86,16) };

	public ArmorRecovery(float radius) {
		super(new Polygon(raw));
		setMoveable(false);
	}

	public void collided(CollisionEvent e) {
		Body a = e.getBodyA();
		Body b = e.getBodyB();
		if (a instanceof Ship) {
			up((Ship) a);
		}
		if (b instanceof Ship) {
			up((Ship) b);
		}
	}

	public ArmorRecovery(float radius, float fixedmass) {
		super(new Circle(radius), fixedmass);
		setMoveable(false);
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle) getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d
				.fillOval((int) (x - r), (int) (y - r), (int) (r * 2),
						(int) (r * 2));
	}

	public float getRadius() {
		return ((Circle) getShape()).getRadius();
	}

	public void up(Ship ship) {
		ship.setArmor(ship.MAX);
	}

	public Body getRemnant() {
		return null;
	}

	public List<Body> getFragments() {
		return null;
	}

	public String getTexturePath() {
		return "pixmaps/armor2.png";
	}

	public Vector2f getTextureCenter() {
		return new Vector2f(38, 37);
	}

	public float getTextureScaleFactor() {
		return .09f;
	}

	public boolean canExplode() {
		return true;
	}
}
