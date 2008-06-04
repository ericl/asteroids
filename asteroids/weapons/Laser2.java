package asteroids.weapons;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public class Laser2 extends Laser {
	public Body getRemnant() {
		return new LargeExplosion();
	}

	public float getDamage() {
		return .6f;
	}

	public Vector2f getTextureCenter() {
		return v(7,7);
	}

	public int getBurstLength() {
		return 5;
	}

	public String getTexturePath() {
		return "pixmaps/blast.png";
	}

	public float getSpeed() {
		return 50;	
	}

	public float getReloadTime() {
		return 700;
	}
}
