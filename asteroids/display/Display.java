package asteroids.display;
import java.awt.RenderingHints;
import java.awt.MediaTracker;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Font;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;

public class Display {
	private Frame frame;
	private BufferStrategy strategy;
	private Graphics2D buf;
	private int width, height;
	private long resizetime = Long.MAX_VALUE;
	private float xo, yo;
	private double sx = 1, sy = 1;
	private Image orig, bg;
	private MediaTracker tracker;
	private HashMap<String,BufferedImage> cache;

	public Display(Frame f) {
		frame = f;
		width = frame.getWidth();
		height = frame.getHeight();
		frame.setIgnoreRepaint(true);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		tracker = new MediaTracker(frame);
		cache = new HashMap<String,BufferedImage>();
		strategy = frame.getBufferStrategy();
		buf = (Graphics2D)strategy.getDrawGraphics();
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				resizetime = System.currentTimeMillis();
				sx = frame.getSize().getWidth() / width;
				sy = frame.getSize().getHeight() / height;
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}

	/**
	 * @param center The vector x*y representing the center of the display.
	 */
	public void setCenter(ROVector2f center) {
		xo = center.getX();
		yo = center.getY();
	}

	/**
	 * @return The vector x*y representing the center of the display.
	 */
	public Vector2f getCenter() {
		return new Vector2f(xo, yo);
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
	 * Tells a drawable object to draw itself.
	 * @param thing The drawable object.
	 */
	public void drawDrawable(Drawable thing) {
		if (isVisible(thing.getPosition(), thing.getRadius()))
			thing.drawTo(buf, xo, yo);
	}

	/**
	 * Draws a Textured object about the center.
	 * @param thing The textured object to be drawn.
	 */
	public void drawTextured(Textured thing) {
		if (!isVisible(thing.getPosition(), thing.getRadius()))
			return;
		BufferedImage i = loadImage(thing.getTexturePath());
		float x = thing.getPosition().getX() - xo;
		float y = thing.getPosition().getY() - yo;
		float scale = thing.getTextureScaleFactor();
		Vector2f c = thing.getTextureCenter();
		// TODO: find some way to cache the rotated images
		AffineTransform trans = AffineTransform.getTranslateInstance
			(x-c.getX()*scale, y-c.getY()*scale);
		trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
		trans.concatenate(AffineTransform.getRotateInstance
			(thing.getRotation(), c.getX(), c.getY()));
		buf.drawImage(i, trans, null);
	}

	/**
	 * @return Valid Graphics2D for current frame only.
	 */
	public Graphics2D getGraphics() {
		return buf;
	}

	/**
	 * Renders the contents of the offscreen buffer to the window and
	 * resets the offscreen buffer to the background.
	 */
	public void show() {
		buf.dispose();
		strategy.show();
		clearBuffer();
		// don't rescale the background while resizing
		if (System.currentTimeMillis() - resizetime > 100) {
			rescaleBackground();
			resizetime = Long.MAX_VALUE;
		}
		buf.scale(sx,sy);
	}

	public void clearBuffer() {
		buf = (Graphics2D)strategy.getDrawGraphics();
		if (bg == null) {
			buf.setColor(Color.white);
			buf.fillRect(0, 0, (int)(sx*width), (int)(sy*height));
		} else
			buf.drawImage(bg,0,0,frame);
		buf.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		buf.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		buf.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	/**
	 * Sets the background image, which will be drawn scaled to the offscreen
	 * buffer before anything else.
	 * @param path Path to the image to be set as the background.
	 */
	public void setBackground(String path) {
		try {
			orig = frame.getToolkit().getImage(path);
		} catch (Exception e) {
			System.out.println("Invalid background path.");
		}
		tracker.addImage(orig, 0);
		rescaleBackground();
	}

	private void rescaleBackground() {
		try {
			bg = orig.getScaledInstance(
			(int)(sx*width),(int)(sy*height),Image.SCALE_FAST);
		} catch (Exception e) {
			System.out.println("No background.");
			return;
		}
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.out.println("E: what?");
		}
	}

	private boolean isVisible(ROVector2f v, float r) {
		float x = v.getX() - xo, y = v.getY() - yo;
		return x > -r && x < width+r && y > -r && y < height+r;
	}

	private BufferedImage loadImage(String path) {
		BufferedImage i = cache.get(path);
		if (i == null)
			try {
				String dir = getClass().getResource("/asteroids/").toString();
				System.out.println("read " + dir + path);
				i = ImageIO.read(new URL(dir+path));
				cache.put(path, i);
			} catch (Exception e) {
				System.out.println("Invalid image path.");
			}
		return i;
	}
}
