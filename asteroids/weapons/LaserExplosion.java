package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class LaserExplosion extends Explosion {
	static double frametime = 100;
	static int frames = 8;
	double inittime = System.currentTimeMillis();
	int frame = 1;

	public float getRadius() {
		return 30;	
	}

	public Vector2f getTextureCenter() {
		return v(21,20);
	}

	public String getTexturePath() {
		if (dead()) return null;
		return "pixmaps/exp2/" + frame + ".png";
	}

	public void endFrame() {
		frame = 1 + (int)((System.currentTimeMillis() - inittime)/frametime*frames);
	}

	public boolean dead() {
		return frame > 8;
	}

	public float getTextureScaleFactor() {
		return 1f;
	}
}
