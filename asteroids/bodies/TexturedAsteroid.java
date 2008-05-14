package asteroids.bodies;
import asteroids.display.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class TexturedAsteroid extends PolyAsteroid implements Textured {
	private String img;

	/**
	 * @param raw The coordinates making up the polygon.
	 * @param img The image the coordinates reflect.
	 * @param nativesize The body's qualitative radius compared to a circle.
	 * @param size The radius of the body to create in relation to nativesize.
	 */
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
