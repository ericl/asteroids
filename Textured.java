/**
 * Something with texture information that can be rendered by a Display.
 */

import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;

public interface Textured {

	/**
	 * Returns constant distance in pixels from the top-left corner
	 * of the texture pixmap to its center of rotation.
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
	 * Returns the maximum visible radius of the rendered texture, not
	 * to be confused with the collidable surface of the body.
	 * @return Maximum visible radius of the texture/body.
	 */
	public float getRadius();

	/**
	 * @return Position as by body.getPosition()
	 */
	public ROVector2f getPosition();

	/**
	 * @return Rotation as by body.getRotation()
	 */
	public float getRotation();
}
