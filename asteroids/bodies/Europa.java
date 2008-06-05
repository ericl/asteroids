package asteroids.bodies;
import java.util.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;

public class Europa extends CircleAsteroid implements Textured {
	private float sphereradius = 150;
	private double crust = 10;
	private IceAsteroid core = new IceAsteroid(140);

	public Europa() {
		super(150, Body.INFINITE_MASS);
	}

	public Vector2f getTextureCenter() {
		return v(150,150);
	}

	public void collided(CollisionEvent event) {
		crust -= Exploder.getDamage(event, this);
	}

	public String getPercentDamage() {
		return (int)(crust*100)/10 + "%";
	}

	public boolean canExplode() {
		return crust < 0;
	}

	public String getTexturePath() {
		return "pixmaps/europa.png";
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		if (getRadius() > 10)
			for (int i=0; i < 6; i++)
				f.add(new HexAsteroid(50));	
		return f;
	}

	public Asteroid getRemnant() {
		return core;
	}
	
	public float getTextureScaleFactor() {
		return sphereradius / 150.0f;
	}
	
	public float getRadius() {
		return sphereradius;
	}
}
