package asteroids.weapons;
import static asteroids.Util.*;
import asteroids.handlers.*;
import net.phys2d.math.*;

public class LaserExplosion extends Explosion {
	static double FRAMETIME = 100;
	static int FRAMES = 8;
	double inittime = Timer.gameTime();
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
		frame = 1 + (int)((Timer.gameTime() - inittime)/FRAMETIME*FRAMES);
	}

	public boolean dead() {
		return frame > FRAMES;
	}

	public float getTextureScaleFactor() {
		return 1f;
	}
}
