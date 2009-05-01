/**
 * "Fireball" type explosion remnant.
 */

package asteroids.weapons;

import static asteroids.Util.*;

import asteroids.handlers.*;

import net.phys2d.raw.*;

import net.phys2d.math.*;

public class LevelUp extends Explosion {
	private World world;
	private long init;

	public LevelUp(World world) {
		super(TrackingMode.ORIGIN);
		init = Timer.gameTime();
		this.world = world;
	}

	public float getRadius() {
		return 250;	
	}

	public Vector2f getTextureCenter() {
		return v(175,400);
	}

	public String getTexturePath() {
		return "pixmaps/levelup.png";
	}

	public void endFrame() {
		super.endFrame();
		if (Timer.gameTime() > 2000 + init)
			world.remove(this);
	}

	public boolean dead() {
		return false;
	}

	public float getTextureScaleFactor() {
		return .5f;
	}
}
