/**
 * Something with texture information that can be rendered by a Display.
 */

package asteroids.display;
import net.phys2d.math.*;

public interface Textured extends Visible {

	/**
	 * Returns constant distance in pixels from the top-left corner
	 * of the texture pixmap to its center of rotation. This should
	 * not be scaled ever.
	 * @return The center of rotation of the texture.
	 */
	public Vector2f getTextureCenter();

	/**
	 * Returns path to the texture file.
	 * @return The path where a file containing the texture is located.
	 */
	public String getTexturePath();

	/**
	 * Returns scale factor for the texture to match the shape exactly.
	 * @return Default scaling multiplier of the texture at 100%.
	 */
	public float getTextureScaleFactor();

	/**
	 * @return Position as by body.getPosition()
	 */
	public ROVector2f getPosition();

	/**
	 * @return Rotation as by body.getRotation()
	 */
	public float getRotation();
}
