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

public class Shield extends Body implements Explodable, Textured, Drawable, Overlay, Targetable, CauseOfDeath {
	protected Visible source;
	protected float damage;
	protected int visible;
	protected float radius;
	protected float MAX = 3f;
	protected World world;

	public String getCause() {
		return "a force field";
	}

	public Shield(Visible source, World world) {
		super(new Circle(source.getRadius() * 4 / 3), 1000);
		assert source instanceof Body;
		if (source instanceof Entity) {
			Entity e = (Entity)source;
			e.registerShield(this);
		}
		addBit(BIT_SHIELD_PENETRATING);
		this.world = world;
		this.radius = source.getRadius() * 4 / 3;
		this.source = source;
		this.addExcludedBody((Body)source);
	}

	public Visible getSource() {
		return source;
	}
	
	public Color getColor() {
		return Color.CYAN;
	}

	public float getMax() {
		return MAX;
	}

	public boolean isVisible() {
		return false;
	}

	public boolean targetableBy(Object o) {
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
		return damage > MAX || visible++ < 0  ? "" : "pixmaps/ship-shield.png";
	}

	public void cloak() {
		visible = -20;
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public Body getRemnant() {
		return new ShieldFailing(source, radius);
	}

	public double health() {
		return Math.max(0, (MAX - damage) / MAX);
	}

	public List<Body> getFragments() {
		return null;
	}

	public boolean canExplode() {
		return damage > MAX;
	}

	public boolean preferDrawableFallback() {
		return false;
	}

	public void endFrame() {
		super.endFrame();
		setPosition(source.getPosition().getX(), source.getPosition().getY());
		if (!world.getBodies().contains((Body)source))
			world.remove(this);
	}

	public void collided(CollisionEvent event) {
		damage += Exploder.getDamage(event, this);
	}
}
