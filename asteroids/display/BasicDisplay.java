package asteroids.display;
import javax.swing.JSplitPane;
import java.awt.RenderingHints;
import java.awt.MediaTracker;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Canvas;
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
import static asteroids.Util.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;

public class BasicDisplay extends Display {
	protected Frame frame;
	protected BufferStrategy strategy;
	protected Graphics2D buf;
	protected Vector2f dim, o = v(0,0), scale = v(1,1);
	protected long resizetime = Long.MAX_VALUE;
	protected Image orig, bg;

	public BasicDisplay(Frame f) {
		frame = f;
		dim = v(frame.getWidth(), frame.getHeight());
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
				double sx = frame.getSize().getWidth() / dim.getX();
				double sy = frame.getSize().getHeight() / dim.getY();
				scale = v(sx, sy);
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
		o = MathUtil.sub(center, MathUtil.scale(dim, .5f));
	}

	public void drawDrawable(Drawable thing) {
		if (isVisible(o, dim, thing.getPosition(), thing.getRadius()))
			thing.drawTo(buf, o);
	}

	public boolean inView(ROVector2f test, float r) {
		return isVisible(o, dim, test, r);
	}

	public void drawTextured(Textured thing) {
		if (isVisible(o, dim, thing.getPosition(), thing.getRadius())) {
			BufferedImage i = loadImage(thing.getTexturePath());
			float x = thing.getPosition().getX() - o.getX();
			float y = thing.getPosition().getY() - o.getY();
			float scale = thing.getTextureScaleFactor();
			Vector2f c = thing.getTextureCenter();
			AffineTransform trans = AffineTransform.getTranslateInstance
				(x-c.getX()*scale, y-c.getY()*scale);
			trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
			trans.concatenate(AffineTransform.getRotateInstance
				(thing.getRotation(), c.getX(), c.getY()));
			buf.drawImage(i, trans, null);
		}
	}

	public Graphics2D getGraphics() {
		return buf;
	}

	public void show() {
		buf.dispose();
		strategy.show();
		clearBuffer();
		// don't rescale the background while resizing
		if (System.currentTimeMillis() - resizetime > 100) {
			rescaleBackground();
			resizetime = Long.MAX_VALUE;
		}
		buf.scale(scale.getX(), scale.getY());
	}

	public void clearBuffer() {
		buf = (Graphics2D)strategy.getDrawGraphics();
		if (bg == null) {
			buf.setColor(Color.black);
			int sx = (int)(scale.getX()*dim.getX());
			int sy = (int)(scale.getY()*dim.getY());
			buf.fillRect(0, 0, sx, sy);
		} else {
			buf.drawImage(bg,0,0,frame);
		}
		buf.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		buf.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		buf.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

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

	protected void rescaleBackground() {
		if (orig == null)
			return;
		int sx = (int)(scale.getX()*dim.getX());
		int sy = (int)(scale.getY()*dim.getY());
		int bgscale = Math.max(sx, sy);
		bg = orig.getScaledInstance(bgscale, bgscale, Image.SCALE_FAST);
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}
}
