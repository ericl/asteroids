/**
 * 	Interface for handling windowing, buffering, and painting
 * 	of worlds, bodies, and strings. In case you want to implement
 * 	an alternate renderer...
 */

package asteroids.display;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public interface Display {

	/**
	 * @param center The vector x*y representing the center of the display.
	 */
	public void setCenter(ROVector2f center);

	/**
	 * @return The vector x*y representing the center of the display.
	 */
	public Vector2f getCenter();

	/**
	 * Draws all drawable bodies in the world to an offscreen buffer.
	 */
	public void drawWorld(World world);

	/**
	 * Tells a drawable object to draw itself.
	 * (do not use except for testing purposes)
	 * @param thing The drawable object.
	 */
	public void drawDrawable(Drawable thing);

	/**
	 * Draws a Textured object about the center.
	 * @param thing The textured object to be drawn.
	 */
	public void drawTextured(Textured thing);

	/**
	 * Draws a colored string at the specified location about the center.
	 * @param text The string to be drawn.
	 * @param font The font of the string.
	 * @param color The color of the string.
	 * @param loc The location of the top-left corner of the string.
	 */
	public void drawString(String text, Font font, Color color, ROVector2f loc);

	/**
	 * Renders the contents of the offscreen buffer to the window and
	 * resets the offscreen buffer to the background.
	 */
	public void show();

	/**
	 * Sets the background image, which will be drawn scaled to the offscreen
	 * buffer before anything else.
	 * @param path Path to the image to be set as the background.
	 */
	public void setBackground(String path);
}
