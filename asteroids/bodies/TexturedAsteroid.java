package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class TexturedAsteroid extends PolyAsteroid implements Textured {
	private String img;

	public TexturedAsteroid(ROVector2f[] raw, String img, float nativesize, float size) {
		super(raw, size / nativesize);
		this.ratio = size / nativesize;
		this.img = img;
	}

	public float getTextureScaleFactor() {
		return ratio;
	}

	public String getTexturePath() {
		return img;
	}
}
