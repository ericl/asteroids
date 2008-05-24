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

public class MPDisplay extends Display {
	private Frame frame;
	private JSplitPane jsplit;
	private BufferStrategy strategyA, strategyB;
	private Canvas A, B;
	private Graphics2D bufA, bufB;
	private Vector2f dim, oA = v(0,0), oB = v(0,0), scale = v(1,1);
	private long resizetime = Long.MAX_VALUE;
	private Image orig, bg;

	public MPDisplay(Frame f, JSplitPane j) {
		frame = f;
		jsplit = j;
		A = (Canvas)j.getLeftComponent();
		B = (Canvas)j.getRightComponent();
		dim = v(frame.getWidth()/2, frame.getHeight());
		frame.setIgnoreRepaint(true);
		A.setIgnoreRepaint(true);
		B.setIgnoreRepaint(true);
		frame.setVisible(true);
		A.createBufferStrategy(2);
		B.createBufferStrategy(2);
		tracker = new MediaTracker(frame);
		cache = new HashMap<String,BufferedImage>();
		strategyA = A.getBufferStrategy();
		strategyB = B.getBufferStrategy();
		bufA = (Graphics2D)strategyA.getDrawGraphics();
		bufB = (Graphics2D)strategyB.getDrawGraphics();
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				resizetime = System.currentTimeMillis();
				double sx = frame.getSize().getWidth() / dim.getX() / 2;
				double sy = frame.getSize().getHeight() / dim.getY();
				scale = v(sx, sy);
				jsplit.setDividerLocation(.5);
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}

	public void setCenter(ROVector2f centerA) {
		oA = MathUtil.sub(centerA, MathUtil.scale(dim, .5f));
	}

	public void setCenter(ROVector2f centerA, ROVector2f centerB) {
		oA = MathUtil.sub(centerA, MathUtil.scale(dim, .5f));
		oB = MathUtil.sub(centerB, MathUtil.scale(dim, .5f));
	}

	public void drawDrawable(Drawable thing) {
		if (isVisible(oA, dim, thing.getPosition(), thing.getRadius()))
			thing.drawTo(bufA, oA);
		if (isVisible(oB, dim, thing.getPosition(), thing.getRadius()))
			thing.drawTo(bufB, oB);
	}

	public void drawTextured(Textured thing) {
		if (isVisible(oA, dim, thing.getPosition(), thing.getRadius())) {
			BufferedImage i = loadImage(thing.getTexturePath());
			float x = thing.getPosition().getX() - oA.getX();
			float y = thing.getPosition().getY() - oA.getY();
			float scale = thing.getTextureScaleFactor();
			Vector2f c = thing.getTextureCenter();
			AffineTransform trans = AffineTransform.getTranslateInstance
				(x-c.getX()*scale, y-c.getY()*scale);
			trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
			trans.concatenate(AffineTransform.getRotateInstance
				(thing.getRotation(), c.getX(), c.getY()));
			bufA.drawImage(i, trans, null);
		}
		if (isVisible(oB, dim, thing.getPosition(), thing.getRadius())) {
			BufferedImage i = loadImage(thing.getTexturePath());
			float x = thing.getPosition().getX() - oB.getX();
			float y = thing.getPosition().getY() - oB.getY();
			float scale = thing.getTextureScaleFactor();
			Vector2f c = thing.getTextureCenter();
			AffineTransform trans = AffineTransform.getTranslateInstance
				(x-c.getX()*scale, y-c.getY()*scale);
			trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
			trans.concatenate(AffineTransform.getRotateInstance
				(thing.getRotation(), c.getX(), c.getY()));
			bufB.drawImage(i, trans, null);
		}
	}

	public Graphics2D getGraphics() {
		return bufA;
	}

	public Graphics2D[] getAllGraphics() {
		Graphics2D[] g2ds = {bufA, bufB};
		return g2ds;
	}

	public void show() {
		bufA.dispose();
		bufB.dispose();
		strategyA.show();
		strategyB.show();
		clearBuffer();
		// don't rescale the background while resizing
		if (System.currentTimeMillis() - resizetime > 100) {
			rescaleBackground();
			resizetime = Long.MAX_VALUE;
		}
		bufA.scale(scale.getX(), scale.getY());
		bufB.scale(scale.getX(), scale.getY());
	}

	public void clearBuffer() {
		bufA = (Graphics2D)strategyA.getDrawGraphics();
		bufB = (Graphics2D)strategyB.getDrawGraphics();
		if (bg == null) {
			bufA.setColor(Color.black);
			bufB.setColor(Color.black);
			int sx = (int)(scale.getX()*dim.getX());
			int sy = (int)(scale.getY()*dim.getY());
			bufA.fillRect(0, 0, sx, sy);
			bufB.fillRect(0, 0, sx, sy);
		} else {
			bufA.drawImage(bg,0,0,frame);
			bufB.drawImage(bg,0,0,frame);
		}
		bufA.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		bufA.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		bufA.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		bufB.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
		bufB.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		bufB.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
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

	private void rescaleBackground() {
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

	public static boolean isVisible(ROVector2f o, ROVector2f dim,
			ROVector2f v, float r) {
		Vector2f rel = MathUtil.sub(v, o);
		return rel.getX() > -r && rel.getX() < dim.getX()+r
			&& rel.getY() > -r && rel.getY() < dim.getY()+r;
	}

	public boolean inView(ROVector2f v, float r) {
		return isVisible(oA, dim, v, r) || isVisible(oB, dim, v, r);
	}
}
