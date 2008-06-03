package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.math.*;

public class LargeExplosion extends Explosion {
	double life = 1;

	public float getRadius() {
		return 46;	
	}

	public Vector2f getTextureCenter() {
		return v(31,31);
	}

	public String getTexturePath() {
		if (dead())
			return null;
		return "pixmaps/exp1/" + (int)life + ".png";
	}

	public void endFrame() {
		life += .1;
	}

	public boolean dead() {
		return life > 11;
	}

	public float getTextureScaleFactor() {
		return 1f;
	}
}
