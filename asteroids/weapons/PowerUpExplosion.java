package asteroids.weapons;
import asteroids.handlers.*;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class PowerUpExplosion extends Explosion {
	static double FRAMETIME = 200;
	static float SCALE = 1.5f;
	static int FRAMES = 5;
	double inittime = Timer.gameTime();
	int frame = 1;

	public PowerUpExplosion() {
		super();
		setEnabled(true);
	}

	public float getRadius() {
		return 12*SCALE;
	}

	public Vector2f getTextureCenter() {
		return v(6*SCALE,7*SCALE);
	}

	public float getRotation() {
		return 0;
	}

	public String getTexturePath() {
		if (dead()) return null;
		return "pixmaps/exp3/" + frame + ".png";
	}

	public void endFrame() {
		frame = 1 + (int)((Timer.gameTime() - inittime)/FRAMETIME*FRAMES);
	}

	public boolean dead() {
		return frame > FRAMES;
	}

	public float getTextureScaleFactor() {
		return SCALE;
	}
}
