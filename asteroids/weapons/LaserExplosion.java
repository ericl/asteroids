/**
 * "Blue flash" explosion remnant.
 */

package asteroids.weapons;

import asteroids.handlers.*;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class LaserExplosion extends Explosion {
	private static double FRAMETIME = 100;
	private static int FRAMES = 8;
	private double inittime = Timer.gameTime();
	private int frame = 1;
	private float scale = 1;

	public LaserExplosion(TrackingMode mode) {
		super(mode);
	}

	public LaserExplosion(TrackingMode mode, float scaler) {
		super(mode);
		scale = scaler;
	}

	public float getRadius() {
		return 30;	
	}

	public Vector2f getTextureCenter() {
		return v(21,20);
	}

	public String getTexturePath() {
		if (dead())
			return null;
		return "pixmaps/exp2/" + frame + ".png";
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
		return scale;
	}
}
