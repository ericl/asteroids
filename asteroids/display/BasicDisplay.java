package asteroids.display;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.net.URL;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.math.*;

public class BasicDisplay extends Display {
	protected BufferStrategy strategy;
	protected Graphics2D buf;
	protected Vector2f origin = v(0,0);
	protected double scale = 1;
	protected long resizetime = Long.MAX_VALUE;
	protected Image orig, bg;

	public BasicDisplay(Frame f, Dimension d) {
		super(f, d);
		frame.setIgnoreRepaint(true);
		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();
		buf = (Graphics2D)strategy.getDrawGraphics();
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				double sx = frame.getSize().getWidth() / ORIGINAL_WIDTH;
				double sy = frame.getSize().getHeight() / ORIGINAL_HEIGHT;
				scale = Math.min(sx, sy);
				dim.setSize(ORIGINAL_WIDTH*sx/scale,ORIGINAL_HEIGHT*sy/scale);
				resizetime = System.currentTimeMillis();
			}
		});
	}

	/**
	 * @param center The vector x*y representing the center of the display.
	 */
	public void setCenter(ROVector2f center) {
		origin = sub(center, scale(v(dim), .5f));
	}

	public void drawDrawable(Drawable thing) {
		if (inView(thing.getPosition(), thing.getRadius()))
			thing.drawTo(buf, origin);
	}

	public boolean inView(ROVector2f test, float r) {
		return inViewFrom(origin, test, r);
	}

	public boolean inViewFrom(ROVector2f o, ROVector2f test, float r) {
		return isVisible(o, dim, test, r);
	}

	public void drawTextured(Textured thing) {
		if (inView(thing.getPosition(), thing.getRadius())) {
			BufferedImage i = loadImage(thing.getTexturePath());
			float x = thing.getPosition().getX() - origin.getX();
			float y = thing.getPosition().getY() - origin.getY();
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
		buf.scale(scale, scale);
	}

	public void clearBuffer() {
		buf = (Graphics2D)strategy.getDrawGraphics();
		if (bg == null) {
			buf.setColor(Color.black);
			int sx = (int)(scale*dim.getWidth());
			int sy = (int)(scale*dim.getHeight());
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
		int sx = (int)(scale*dim.getWidth());
		int sy = (int)(scale*dim.getHeight());
		int bgscale = Math.max(sx, sy);
		bg = orig.getScaledInstance(bgscale, bgscale, Image.SCALE_FAST);
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}

	public ROVector2f getOffscreenCoords(float r, float b, ROVector2f o) {
		ROVector2f v = o;
		while (true) {
			float x = range(-b-dim.getWidth(), b+dim.getWidth());
			float y = range(-b-dim.getHeight(), b+dim.getHeight());
			v = MathUtil.sub(o, v(-x-r, -y-r));
			if (!inView(v,r))
				return v;
		}
	}
}
