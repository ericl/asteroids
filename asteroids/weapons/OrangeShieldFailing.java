/**
 * "Orange flash" explosion remnant.
 */

package asteroids.weapons;

import asteroids.handlers.*;

import asteroids.display.*;

import net.phys2d.math.*;

import static asteroids.Util.*;

public class OrangeShieldFailing extends Explosion {
	private static double FRAMETIME = 200;
	private static int FRAMES = 8;
	private double inittime = Timer.gameTime();
	private int frame = 1;
	private float scale = 1;
	private Visible ship;

	public OrangeShieldFailing(Visible ship, float radius) {
		super(TrackingMode.NONE);
		this.ship = ship;
		scale = radius / 10;
	}

	public float getRadius() {
		return 30 * scale;
	}

	public Vector2f getTextureCenter() {
		return v(21,20);
	}

	public String getTexturePath() {
		if (dead())
			return null;
		return "pixmaps/exp2-o/" + frame + ".png";
	}

	private void recalcFrame() {
		frame = 1 + (int)((Timer.gameTime() - inittime)/FRAMETIME*FRAMES);
	}

	public void endFrame() {
		super.endFrame();
		recalcFrame();
		setPosition(ship.getPosition().getX(), ship.getPosition().getY());
	}

	public boolean dead() {
		recalcFrame();
		return frame > FRAMES;
	}

	public float getTextureScaleFactor() {
		return scale;
	}
}
