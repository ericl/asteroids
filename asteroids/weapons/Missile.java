package asteroids.weapons;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;


public class Missile extends Body implements Textured {
	private static ROVector2f[] mShape = { v(0,0), v(4,0), v(0,-6), v(-8,0), v(0,6), v(4,4) };
	
	public Missile() {
		super(new Polygon(mShape), 10);
	}
	
	public String getTexturePath() {
		return "pixmaps/circle1.png";
	}

	public float getTextureScaleFactor() {
		return 10 / 41.0f;
	}

	public Vector2f getTextureCenter() {
		return v(0,0);
	}

	public float getRadius() {
		return 0;
	}
}
