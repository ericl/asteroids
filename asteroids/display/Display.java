/**
 * Draws onto component(s) that support createBufferStrategy().
 * All components should be the same size.
 */

package asteroids.display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.geom.AffineTransform;

import java.awt.image.BufferStrategy;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.World;

import static asteroids.Util.*;

import static net.phys2d.math.MathUtil.*;

public class Display {
	protected BufferStrategy[] strategies;
	protected Component[] screens;
	protected Frame frame;
	protected Graphics2D[] bufs;
	protected HashMap<String,Image> cache;
	protected Image bg;
	protected MediaTracker tracker;
	protected ROVector2f[] centers;
	protected String bgpath;
	protected Dimension dim;
	protected String dir = getClass().getResource("/asteroids/").toString();
	protected int ORIGINAL_WIDTH, ORIGINAL_HEIGHT;
	protected int index = 1; // mediatracker image loading
	protected int n; // num views
	protected long resizetime = Long.MAX_VALUE;

	/**
	 * Instantiate a display that draws onto a frame.
	 * Recommended for full-screen games only.
	 */
	public Display(Frame f, Dimension d) {
		init(f, d, new Component[]{f});
		f.createBufferStrategy(2);
		strategies[0] = f.getBufferStrategy();
		getNewBufs();
	}

	/**
	 * Instantiate a display that draws onto canvases.
	 */
	public Display(Frame f, Dimension d, Canvas ... canvases) {
		init(f, d, canvases);
		for (int i=0; i < n; i++) {
			canvases[i].createBufferStrategy(2);
			strategies[i] = canvases[i].getBufferStrategy();
		}
		getNewBufs();
	}

	/**
	 * Common construction operations. Does not initialize buffers.
	 */
	protected void init(Frame frame, final Dimension dim, final Component[] screens) {
		n = screens.length;
		if (n < 1)
			throw new IllegalArgumentException("Illegal number of screens.");
		this.frame = frame;
		this.dim = dim;
		this.screens = screens;
		strategies = new BufferStrategy[n];
		centers = new ROVector2f[n];
		bufs = new Graphics2D[n];
		ORIGINAL_WIDTH = (int)dim.getWidth();
		ORIGINAL_HEIGHT = (int)dim.getHeight();
		cache = new HashMap<String,Image>();
		tracker = new MediaTracker(frame);
		for (int i=0; i < n; i++) {
			centers[i] = v(0,0);
			screens[i].setIgnoreRepaint(true);
		}
		frame.setVisible(true);
		frame.setIgnoreRepaint(true);
		dim.setSize(screens[0].getSize()); // sometimes componentResized() is skipped?
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				dim.setSize(screens[0].getSize()); // assume all are the same size
				resizetime = System.currentTimeMillis();
			}
		});
	}

	/**
	 * Draws all drawable bodies in the world to an offscreen buffer.
	 */
	public void drawWorld(World world) {
		BodyList bodies = world.getBodies();
		List<Body> top = new ArrayList<Body>();
		for (int i=0; i < bodies.size(); i++) {
			if (bodies.get(i) instanceof Overlay)
				top.add(bodies.get(i));
			else if (bodies.get(i) instanceof Textured)
				drawTextured((Textured)bodies.get(i));
			else if (bodies.get(i) instanceof Drawable)
				drawDrawable((Drawable)bodies.get(i));
		}
		for (Body b : top) {
			if (b instanceof Textured)
				drawTextured((Textured)b);
			else if (b instanceof Drawable)
				drawDrawable((Drawable)b);
		}
	}

	/**
	 * @param	updates	The new origin(s) of the display.
	 */
	public void setCenter(ROVector2f ... updates) {
		for (int i=0; i < n; i++)
			centers[i] = sub(updates[i], scale(v(dim), .5f));
	}

	/**
	 * Tells a drawable object to draw itself.
	 * @param	thing	The drawable object.
	 */
	public void drawDrawable(Drawable thing) {
		for (int i=0; i < n; i++)
			if (isVisible(centers[i], dim, thing.getPosition(), thing.getRadius())) {
				bufs[i].setColor(thing.getColor());
				thing.drawTo(bufs[i], centers[i]);
			}
	}

	/**
	 * Draws a Textured object about the center.
	 * @param	thing	The textured object to be drawn.
	 */
	public void drawTextured(Textured thing) {
		Image img = loadImage(thing.getTexturePath());
		float scale = thing.getTextureScaleFactor();
		Vector2f c = thing.getTextureCenter();
		for (int i=0; i < n; i++)
			if (isVisible(centers[i], dim, thing.getPosition(), thing.getRadius())) {
				float x = thing.getPosition().getX() - centers[i].getX();
				float y = thing.getPosition().getY() - centers[i].getY();
				AffineTransform trans = AffineTransform.getTranslateInstance
					(x-c.getX()*scale, y-c.getY()*scale);
				trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
				trans.concatenate(AffineTransform.getRotateInstance
					(thing.getRotation(), c.getX(), c.getY()));
				bufs[i].drawImage(img, trans, null);
			}
	}

	/**
	 * Renders the contents of the offscreen buffer to the window and
	 * resets the offscreen buffer to the background.
	 */
	public void show() {
		for (Graphics2D g2d : bufs)
			g2d.dispose();
		for (BufferStrategy s : strategies)
			s.show();
		getNewBufs();
		drawBackground();
		// don't rescale the background while resizing
		if (System.currentTimeMillis() - resizetime > 100) {
			rescaleBackground();
			resizetime = Long.MAX_VALUE;
		}
	}

	/**
	 * Gets new accelerated buffers from the bufferstrategies.
	 */
	protected void getNewBufs() {
		for (int i=0; i < n; i++) {
			bufs[i] = (Graphics2D)strategies[i].getDrawGraphics();
			bufs[i].setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		}
	}

	/**
	 * Draws specified background image to all buffers or solid black.
	 */
	protected void drawBackground() {
		for (Graphics2D g2d : bufs) {
			if (bg == null) {
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
			} else
				g2d.drawImage(bg, 0, 0, frame);
		}
	}

	/**
	 * Rescale the background to fit changing frame size.
	 */
	protected void rescaleBackground() {
		if (bgpath == null)
			return;
		int max = Math.max((int)dim.getWidth(), (int)dim.getHeight());
		bg = loadImage(bgpath).getScaledInstance(max, max, Image.SCALE_FAST);
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}

	/**
	 * Returns drawing buffer currently used by the display.
	 * @return	Graphics2D(s) for the current frame
	 */
	public Graphics2D[] getGraphics() {
		return bufs;	
	}

	/**
	 * @return	True if the object is visible from the last set center(s).
	 * @param	test	The coordinate of the object.
	 * @param	r	The radius of the object.
	 */
	public boolean inView(ROVector2f test, float r) {
		return inView(test, r, centers);
	}

	/**
	 * @return	True if the object is visible from the last set center(s).
	 * @param	test	The coordinate of the object.
	 * @param	r	The radius of the object.
	 * @param	origins	The origins to be considered.
	 */
	public boolean inView(ROVector2f test, float r, ROVector2f ... origins) {
		for (int i=0; i < origins.length; i++)
			if (inViewFrom(origins[i], test, r))
				return true;
		return false;
	}

	/**
	 * @param	o	The origin used for testing viewability.
	 */
	public boolean inViewFrom(ROVector2f o, ROVector2f test, float r) {
		return isVisible(o, dim, test, r);
	}

	/**
	 * Sets the background image, which will be drawn scaled to the offscreen
	 * buffer before anything else.
	 * @param	path	Path to the image to be set as the background.
	 */
	public void setBackground(String path) {
		try {
			bgpath = path;
			tracker.addImage(loadImage(path), 0);
			rescaleBackground();
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Invalid background path.");
		}
	}

	/**
	 * @return Image from a path.
	 * @param	path	The path of the image to be loaded.
	 */
	protected Image loadImage(String path) {
		Image i = cache.get(path);
		if (i == null)
			try {
				i = frame.getToolkit().createImage(new URL(dir+path));
				cache.put(path, i);
				tracker.addImage(i, index++);
				tracker.waitForAll();
			} catch (Exception e) {
				System.err.println(e);
			}
		return i;
	}

	/**
	 * @param	o	The center of the screen.
	 * @param	dim	The dimensions of the screen.
	 * @param	v	The absolute location of the object.
	 * @param	r	The visible radius of the object.
	 */
	protected static boolean isVisible(ROVector2f o, Dimension dim,
			ROVector2f v, float r) {
		Vector2f rel = sub(v, o);
		return rel.getX() > -r && rel.getX() < dim.getWidth()+r
			&& rel.getY() > -r && rel.getY() < dim.getHeight()+r;
	}

	/**
	 * Get offscreen coords for a shape of radius r.
	 * @param	r	The radius of the new object.
	 * @param	b	The maximum distance from the display boundary.
	 * @param	o	The origin of the area to be considered.
	 */
	public ROVector2f getOffscreenCoords(float r, float b, ROVector2f o) {
		ROVector2f v = o;
		while (true) {
			float x = range(-b-dim.getWidth()/2, b+dim.getWidth()*3/2);
			float y = range(-b-dim.getHeight()/2, b+dim.getHeight()*3/2);
			v = sub(o, v(-x-r, -y-r));
			if (!inView(v,r))
				return v;
		}
	}

	/**
	 * @return Dimension of an arbitrary component, updated as it changes size.
	 * Behavior will be unpredictable if the components aren't the same size.
	 */
	public Dimension getDimension() {
		return dim;
	}

	/**
	 * @param modifier Value to be summed with the return value.
	 * @return Width of the frame with a modifier.
	 */
	public int w(int modifier) {
		return (int)(dim.getWidth()+modifier);
	}

	/**
	 * @param modifier Value to be summed with the return value.
	 * @return Height of the frame with a modifier.
	 */
	public int h(int modifier) {
		return (int)(dim.getHeight()+modifier);
	}
}
