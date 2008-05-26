package asteroids.bodies;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

public class HexAsteroid extends PolyAsteroid {
	protected boolean explode;

	// kinda like a smashed hexagon
	private static ROVector2f[] geo = {v(-30,0),v(-10,-10),v(10,-10),v(30,0),v(10,20),v(-10,20)};

	public HexAsteroid(float size) {
		super(geo, size / 25);
	}

	public boolean canExplode() {
		return explode;
	}

	public void collided(CollisionEvent event) {
		if (Exploder.worthyCollision(event))
			explode = true;
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new HexAsteroid(getRadius() / 2) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		HexAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new HexAsteroid(getRadius() / 3);
				tmp.setColor(color);
				f.add(tmp);
			}
		return f;
	}
}
