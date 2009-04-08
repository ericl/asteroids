/**
 * "Fireball" type explosion remnant.
 */

package asteroids.weapons;
import net.phys2d.math.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public class LargeExplosion extends Explosion {
	private static double FRAMETIME = 500;
	private static int FRAMES = 11;
	private double inittime = Timer.gameTime();
	private int frame = 1;
	private float scale = 1;

	public LargeExplosion(TrackingMode mode, float scaler) {
		super(mode);
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
