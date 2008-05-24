package asteroids.display;
import java.awt.Image;
import java.awt.Color;
import java.awt.MediaTracker;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;
import java.util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public abstract class Display {
	protected MediaTracker tracker;
	protected HashMap<String,BufferedImage> cache;

	/**
	 * Draws all drawable bodies in the world to an offscreen buffer.
	 */
	public void drawWorld(World world) {
		BodyList bodies = world.getBodies();
		for (int i=0; i < bodies.size(); i++)
			if (bodies.get(i) instanceof Textured)
				drawTextured((Textured)bodies.get(i));
			else if (bodies.get(i) instanceof Drawable)
				drawDrawable((Drawable)bodies.get(i));
	}

	/**
	 * @param center The new origin of the display.
	 */
	public abstract void setCenter(ROVector2f center);

	/**
	 * Tells a drawable object to draw itself.
	 * @param thing The drawable object.
	 */
	public abstract void drawDrawable(Drawable thing);

	/**
	 * Draws a Textured object about the center.
	 * @param thing The textured object to be drawn.
	 */
	public abstract void drawTextured(Textured thing);

	/**
	 * Renders the contents of the offscreen buffer to the window and
	 * resets the offscreen buffer to the background.
	 */
	public abstract void show();

	/**
	 * @return Valid Graphics2D object for the current frame.
	 */
	public abstract Graphics2D getGraphics();

	/**
	 * @return True if the object is visible from the last set center.
	 * @param test The coordinate of the object.
	 * @param r The radius of the object.
	 */
	public abstract boolean inView(ROVector2f test, float r);

	/**
	 * Sets the background image, which will be drawn scaled to the offscreen
	 * buffer before anything else.
	 * @param path Path to the image to be set as the background.
	 */
	public abstract	void setBackground(String path);

	protected BufferedImage loadImage(String path) {
		BufferedImage i = cache.get(path);
		if (i == null)
			try {
				String dir = getClass().getResource("/asteroids/").toString();
				i = ImageIO.read(new URL(dir+path));
				cache.put(path, i);
			} catch (Exception e) {
				System.err.println(e);
				System.err.println("Invalid image path.");
			}
		return i;
	}
}
