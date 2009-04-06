/**
 * Fuzzy-looking asteroids that shrinks instead of exploding.
 */

package asteroids.bodies;
import java.util.*;
import java.awt.Color;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class IceAsteroid extends CircleAsteroid implements Textured {
	private double melting;
	private int count = 0;
	private boolean rocky = oneIn(2);
	private static final int ICE_BOUND_MIN = 13;
	private int ICE_TO_ROCK_RADIUS = (int)range(20,100);

	public IceAsteroid(float radius) {
		super(radius);
		color = Color.CYAN;
	}

	public Vector2f getTextureCenter() {
		return v(51,51);
	}

	public float getTextureScaleFactor() {
		return getRadius()/72f;
	}

	public String getTexturePath() {
		return "pixmaps/fog.png";
	}

	public void endFrame() {
		super.endFrame();
		count++;
		if (melting - 1 > 0 && count % 5 == 0) {
			melting--;
			if (rocky && getRadius() > ICE_TO_ROCK_RADIUS)
				setShape(new Circle(getRadius()*2/3 - 1));
			else if (!rocky && getRadius() > ICE_BOUND_MIN)
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
		return getRadius() < ICE_BOUND_MIN || (rocky && getRadius() <= ICE_TO_ROCK_RADIUS);
	}

	public Body getRemnant() {
		return rocky ? null : PowerUp.random();
	}

	public List<Body> getFragments() {
		if (!rocky || getRadius() < ICE_BOUND_MIN)
			return null;
		List<Body> f = new ArrayList<Body>(6);
		damage = Float.POSITIVE_INFINITY;
		Body tmp;
		for (int i=0; i < 4; i++) {
			tmp = new SmallAsteroid(getRadius() / 3);
			f.add(tmp);
		}
		for (int i=0; i < 4; i++) {
			tmp = new IceAsteroid(getRadius() / 6);
			f.add(tmp);
		}
		return f;
	}
}
