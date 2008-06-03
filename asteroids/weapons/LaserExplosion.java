package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class LaserExplosion extends Explosion {
	double life = 1;

	public float getRadius() {
		return 30;	
	}

	public Vector2f getTextureCenter() {
		return v(21,21);
	}

	public String getTexturePath() {
		if (dead())
			return null;
		return "pixmaps/exp2/" + (int)life + ".png";
	}

	public void endFrame() {
		life += .3;
	}

	public boolean dead() {
		return life > 8;
	}

	public float getTextureScaleFactor() {
		return 1f;
	}
}
