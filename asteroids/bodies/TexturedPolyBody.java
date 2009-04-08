/**
 * Polygonal asteroid that also implements Textured.
 */

package asteroids.bodies;

import net.phys2d.math.*;

import asteroids.display.*;

public abstract class TexturedPolyBody extends PolyBody implements Textured {
	protected String img;

	/**
	 * @param	raw	The coordinates making up the polygon.
	 * @param	img	The image the coordinates reflect.
	 * @param	nativesize	Radius that makes this similar in size to a same-radius circle.
	 * @param	size	The radius of the body to create in relation to nativesize.
	 */
	public TexturedPolyBody(ROVector2f[] raw, String img, float nativesize, float size) {
		super(raw, size / nativesize);
		this.ratio = size / nativesize;
		this.img = img;
	}

	/**
	 * @param	mass	The mass of the body
	 */
	public TexturedPolyBody(ROVector2f[] raw, String img, float nativesize, float size, float mass) {
		super(raw, size / nativesize, mass);
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
