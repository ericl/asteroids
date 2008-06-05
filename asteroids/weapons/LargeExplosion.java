package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class LargeExplosion extends Explosion {
	static double frametime = 500;
	static int frames = 11;
	double inittime = System.currentTimeMillis();
	int frame = 1;
	float scale = 1;

	public LargeExplosion() {
		super();
	}

	public LargeExplosion(float scaler) {
		super();
		scale = scaler;
	}

	public float getRadius() {
		return 46;	
	}

	public Vector2f getTextureCenter() {
		return v(31,31);
	}

	public String getTexturePath() {
		if (frame > 11)
			return null;
		return "pixmaps/exp1/" + frame + ".png";
	}

	public void endFrame() {
		frame = 1 + (int)((System.currentTimeMillis() - inittime)/frametime*frames);
	}

	public boolean dead() {
		return frame > 11;
	}

	public float getTextureScaleFactor() {
		return scale;
	}
}
