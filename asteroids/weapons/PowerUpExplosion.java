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

	public PowerUpExplosion(TrackingMode mode) {
		super(mode);
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
		if (dead())
			return null;
		return "pixmaps/exp3/" + frame + ".png";
	}

	private void recalcFrame() {
		frame = 1 + (int)((Timer.gameTime() - inittime)/FRAMETIME*FRAMES);
	}

	public void endFrame() {
		super.endFrame();
		recalcFrame();
	}

	public boolean dead() {
		recalcFrame();
		return frame > FRAMES;
	}

	public float getTextureScaleFactor() {
		return SCALE;
	}
}
