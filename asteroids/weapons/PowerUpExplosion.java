package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class PowerUpExplosion extends Explosion {
	static double FRAMETIME = 200;
	static int FRAMES = 5;
	double inittime = System.currentTimeMillis();
	int frame = 1;

	public PowerUpExplosion() {
		super();
		setEnabled(true);
	}

	public float getRadius() {
		return 12;	
	}

	public Vector2f getTextureCenter() {
		return v(6,7);
	}

	public float getRotation() {
		return 0;
	}

	public String getTexturePath() {
		if (dead()) return null;
		return "pixmaps/exp3/" + frame + ".png";
	}

	public void endFrame() {
		frame = 1 + (int)((System.currentTimeMillis() - inittime)/FRAMETIME*FRAMES);
	}

	public boolean dead() {
		return frame > FRAMES;
	}

	public float getTextureScaleFactor() {
		return 1.3f;
	}
}
