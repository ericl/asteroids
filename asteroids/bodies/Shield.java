package asteroids.bodies;

import java.awt.*;

import java.util.List;

import asteroids.ai.*;
import asteroids.display.*;
import asteroids.weapons.*;
import asteroids.handlers.Exploder;

import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;

import static asteroids.Util.*;

public class Shield extends Body implements Explodable, Textured, Drawable, Overlay, Targetable {
	protected Entity ship;
	protected float damage;
	protected float radius;

	public Shield(Entity ship) {
		super(new Circle(ship.getRadius() * 4 / 3), 1000);
		addBit(1l);
		this.radius = ship.getRadius() * 4 / 3;
		this.ship = ship;
		this.addExcludedBody((Body)ship);
	}

	public Entity getShip() {
		return ship;
	}
	
	public Color getColor() {
		return Color.CYAN;
	}

	public float getMax() {
		return 4;
	}

	public boolean canTarget() {
		return false;
	}

	public int getPointValue() {
		return 0;
	}

	public float getRadius() {
		return radius;
	}

	public Vector2f getTextureCenter() {
		return v(27,27);
	}

	public float getTextureScaleFactor() {
		return radius / 27;
	}

	public String getTexturePath() {
		return damage > getMax() ? "" : "pixmaps/ship-shield.png";
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public Body getRemnant() {
		return new ShieldFailing(ship, radius);
	}

	public double health() {
		return Math.max(0, (getMax() - damage) / getMax());
	}

	public List<Body> getFragments() {
		return null;
	}

	public boolean canExplode() {
		return damage > getMax();
	}

	public boolean preferDrawableFallback() {
		return false;
	}

	public void endFrame() {
		super.endFrame();
		setPosition(ship.getPosition().getX(), ship.getPosition().getY());
		if (!ship.getWorld().getBodies().contains((Body)ship))
			ship.getWorld().remove(ship);
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}
}
