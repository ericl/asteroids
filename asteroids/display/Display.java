package asteroids.display;
import java.awt.MediaTracker;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Frame;
import java.awt.image.*;
import java.net.URL;
import javax.imageio.*;
import java.util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;

public abstract class Display {
	protected MediaTracker tracker;
	protected HashMap<String,BufferedImage> cache;
	protected Frame frame;
	protected final Dimension dim;
	protected final int ORIGINAL_WIDTH, ORIGINAL_HEIGHT;

	public Display(Frame f, Dimension d) {
		frame = f;
		dim = d;
		ORIGINAL_WIDTH = (int)dim.getWidth();
		ORIGINAL_HEIGHT = (int)dim.getHeight();
		cache = new HashMap<String,BufferedImage>();
		tracker = new MediaTracker(frame);
		frame.setVisible(true);
	}

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
	 * @return Valid Graphics2D for the current frame.
	 */
	public abstract Graphics2D getGraphics();

	/**
	 * @return True if the object is visible from the last set center.
	 * @param test The coordinate of the object.
	 * @param r The radius of the object.
	 */
	public abstract boolean inView(ROVector2f test, float r);

	/**
	 * @param o The origin used for testing viewability.
	 */
	public abstract boolean inViewFrom(ROVector2f o, ROVector2f test, float r);

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


	/**
	 * @param o The center of the screen.
	 * @param dim The dimensions of the screen.
	 * @param v The absolute location of the object.
	 * @param r The visible radius of the object.
	 */
	protected static boolean isVisible(ROVector2f o, Dimension dim,
			ROVector2f v, float r) {
		Vector2f rel = MathUtil.sub(v, o);
		return rel.getX() > -r && rel.getX() < dim.getWidth()+r
			&& rel.getY() > -r && rel.getY() < dim.getHeight()+r;
	}

	/**
	 * Get offscreen coords for a shape of radius r.
	 * @param r The radius of the new object.
	 * @param b The maximum distance from the display boundary.
	 * @param o The origin of the area to be considered.
	 */
	public abstract ROVector2f getOffscreenCoords( float r, float b, ROVector2f o);

	public int w(int modifier) {
		return (int)(dim.getWidth()+modifier);
	}

	public int h(int modifier) {
		return (int)(dim.getHeight()+modifier);
	}
}
