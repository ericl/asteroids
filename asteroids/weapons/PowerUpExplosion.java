/**
 * Explosion for destruction of powerups.
 */

package asteroids.weapons;

import asteroids.handlers.*;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class PowerUpExplosion extends Explosion {
	private static double FRAMETIME = 200;
	private static float SCALE = 1.5f;
	private static int FRAMES = 5;
	private double inittime = Timer.gameTime();
	private int frame = 1;

	public PowerUpExplosion() {
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
