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
	private boolean noaa, notex, nobg, nofilter;
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
		frame.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'a': noaa = !noaa; break;
					case 't': notex = !notex; break;
					case 'b': nobg = !nobg; break;
					case 'f': nofilter = !nofilter; break;
					case '1': high(); break;
					case '2': med(); break;
					case '3': bare(); break;
				}
			}
			private void high() {
				noaa = false;
				notex = false;
				nobg = false;
				nofilter = false;
			}
			private void med() {
				noaa = true;
				notex = false;
				nobg = false;
				nofilter = true;
			}
			private void bare() {
				noaa = true;
				notex = true;
				nobg = true;
				nofilter = true;
			}
		});
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
		xo = center.getX() - width / 2;
		yo = center.getY() - height / 2;
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
		for (int i=0; i < bodies.size(); i++) {
			if (notex) {
				if (bodies.get(i) instanceof Drawable)
					drawDrawable((Drawable)bodies.get(i));
				else if (bodies.get(i) instanceof Textured)
					drawTextured((Textured)bodies.get(i));
			} else {
				if (bodies.get(i) instanceof Textured)
					drawTextured((Textured)bodies.get(i));
				else if (bodies.get(i) instanceof Drawable)
					drawDrawable((Drawable)bodies.get(i));
			}
		}
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
		if (bg == null || nobg) {
			buf.setColor(Color.black);
			buf.fillRect(0, 0, (int)(sx*width), (int)(sy*height));
		} else
			buf.drawImage(bg,0,0,frame);
		buf.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 (noaa ? RenderingHints.VALUE_ANTIALIAS_OFF :
		       RenderingHints.VALUE_ANTIALIAS_ON));
		buf.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		 (nofilter ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
			         RenderingHints.VALUE_INTERPOLATION_BILINEAR));
		buf.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		 (noaa ? RenderingHints.VALUE_TEXT_ANTIALIAS_OFF :
		       RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
	}

	/**
	 * Sets the background image, which will be drawn scaled to the offscreen
	 * buffer before anything else.
	 * @param path Path to the image to be set as the background.
	 */
	public void setBackground(String path) {
		try {
			String dir = getClass().getResource("/asteroids/").toString();
			// for some reason BufferedImages are *very* slow here
			orig = frame.getToolkit().getImage(new URL(dir+path));
			tracker.addImage(orig, 0);
			rescaleBackground();
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Invalid background path.");
		}
	}

	private void rescaleBackground() {
		if (orig == null)
			return;
		bg = orig.getScaledInstance(
		    (int)(sx*width),(int)(sy*height),Image.SCALE_FAST);
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}

	/**
	 * @param v The absolute location of the object.
	 * @param r The visible radius of the object.
	 */
	public boolean isVisible(ROVector2f v, float r) {
		float x = v.getX() - xo, y = v.getY() - yo;
		return x > -r && x < width+r && y > -r && y < height+r;
	}

	private BufferedImage loadImage(String path) {
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
