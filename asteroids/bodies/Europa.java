package asteroids.bodies;
import asteroids.*;
import java.util.*;
import java.awt.Color;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;

public class Europa extends CircleAsteroid implements Textured {
	private IceAsteroid core = new IceAsteroid(90);
	public static int MAX = 10;

	public Europa() {
		super(150);
		setMoveable(false);
	}

	public Vector2f getTextureCenter() {
		return v(150,150);
	}

	public Color statusColor() {
		if (damage/MAX < .4)
			return AbstractGame.COLOR;
		else if (damage/MAX < .8)
			return Color.YELLOW;
		return Color.RED;
	}
	
	public boolean canExplode() {
		return damage > MAX;
	}

	public String getPercentDamage() {
		return (int)((1-damage/MAX)*100) + "%";
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
		core.setMoveable(false);
		return core;
	}
	
	public float getTextureScaleFactor() {
		return getRadius() / 150.0f;
	}
}
