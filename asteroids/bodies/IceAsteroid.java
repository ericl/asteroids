package asteroids.bodies;
import asteroids.handlers.*;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import static asteroids.Util.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class IceAsteroid extends CircleAsteroid implements Textured {
	private double melting;
	private int count = 0;

	public IceAsteroid(float radius) {
		super(radius);
	}

	public Vector2f getTextureCenter() {
		return v(100,100);
	}

	public float getTextureScaleFactor() {
		return getRadius()/66.66f;
	}

	public String getTexturePath() {
		return "pixmaps/fog.png";
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Circle circle = (Circle)getShape();
		g2d.setColor(Color.CYAN);
		float x = getPosition().getX() - o.getX();
		float y = getPosition().getY() - o.getY();
		float r = circle.getRadius();
		g2d.fillOval((int) (x-r),(int) (y-r),(int) (r*2),(int) (r*2));
	}

	public void endFrame() {
		super.endFrame();
		count++;
		if (melting - 1 > 0 && count % 5 == 0) {
			melting--;
			if (getRadius() > 5)
				setShape(new Circle(getRadius()*2/3 - 1));
		}
	}

	public float getRadius() {
		return super.getRadius()/2*3;
	}

	public void collided(CollisionEvent e) {
		melting += 50 * Exploder.getDamage(e, this);
	}

	public boolean canExplode() {
		return getRadius() < 5;
	}

	public Body getRemnant() {
		return PowerUp.random();
	}

	public List<Body> getFragments() {
		return null;
	}
}
