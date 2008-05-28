package asteroids.display;
import javax.swing.JSplitPane;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import static asteroids.Util.*;
import static net.phys2d.math.MathUtil.*;
import net.phys2d.math.*;

public class MPDisplay extends Display {
	protected JSplitPane jsplit;
	protected BufferStrategy strategyA, strategyB;
	protected Canvas A, B;
	protected Graphics2D bufA, bufB;
	protected Vector2f oA = v(0,0), oB = v(0,0);
	protected double scale = 1;
	protected long resizetime = Long.MAX_VALUE;
	protected String bgpath;
	protected Image bg;

	public MPDisplay(Frame f, JSplitPane j, Dimension d) {
		super(f, d);
		jsplit = j;
		A = (Canvas)j.getLeftComponent();
		B = (Canvas)j.getRightComponent();
		frame.setIgnoreRepaint(true);
		A.setIgnoreRepaint(true);
		B.setIgnoreRepaint(true);
		A.createBufferStrategy(2);
		B.createBufferStrategy(2);
		tracker = new MediaTracker(frame);
		strategyA = A.getBufferStrategy();
		strategyB = B.getBufferStrategy();
		bufA = (Graphics2D)strategyA.getDrawGraphics();
		bufB = (Graphics2D)strategyB.getDrawGraphics();
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				double sx = frame.getSize().getWidth() / ORIGINAL_WIDTH / 2;
				double sy = frame.getSize().getHeight() / ORIGINAL_HEIGHT;
				scale = Math.min(sx, sy);
				dim.setSize(ORIGINAL_WIDTH*sx/scale,ORIGINAL_HEIGHT*sy/scale);
				resizetime = System.currentTimeMillis();
				jsplit.setDividerLocation(.5);
			}
		});
	}

	public void setCenter(ROVector2f centerA) {
		oA = sub(centerA, scale(v(dim), .5f));
	}

	public void setCenter(ROVector2f centerA, ROVector2f centerB) {
		oA = sub(centerA, scale(v(dim), .5f));
		oB = sub(centerB, scale(v(dim), .5f));
	}

	public void drawDrawable(Drawable thing) {
		if (isVisible(oA, dim, thing.getPosition(), thing.getRadius()))
			thing.drawTo(bufA, oA);
		if (isVisible(oB, dim, thing.getPosition(), thing.getRadius()))
			thing.drawTo(bufB, oB);
	}

	public void drawTextured(Textured thing) {
		Image i = loadImage(thing.getTexturePath());
		float scale = thing.getTextureScaleFactor();
		Vector2f c = thing.getTextureCenter();
		if (isVisible(oA, dim, thing.getPosition(), thing.getRadius())) {
			float x = thing.getPosition().getX() - oA.getX();
			float y = thing.getPosition().getY() - oA.getY();
			AffineTransform trans = AffineTransform.getTranslateInstance
				(x-c.getX()*scale, y-c.getY()*scale);
			trans.concatenate(AffineTransform.getScaleInstance(scale, scale));
			trans.concatenate(AffineTransform.getRotateInstance
				(thing.getRotation(), c.getX(), c.getY()));
			bufA.drawImage(i, trans, null);
		}
		if (isVisible(oB, dim, thing.getPosition(), thing.getRadius())) {
			float x = thing.getPosition().getX() - oB.getX();
			float y = thing.getPosition().getY() - oB.getY();
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
		bufA.scale(scale, scale);
		bufB.scale(scale, scale);
	}

	public void clearBuffer() {
		bufA = (Graphics2D)strategyA.getDrawGraphics();
		bufB = (Graphics2D)strategyB.getDrawGraphics();
		if (bg == null) {
			bufA.setColor(Color.black);
			bufB.setColor(Color.black);
			int sx = (int)(scale*dim.getWidth());
			int sy = (int)(scale*dim.getHeight());
			bufA.fillRect(0, 0, sx, sy);
			bufB.fillRect(0, 0, sx, sy);
		} else {
			bufA.drawImage(bg,0,0,frame);
			bufB.drawImage(bg,0,0,frame);
		}
		bufA.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_SPEED);
		bufB.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_SPEED);
	}

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

	protected void rescaleBackground() {
		if (bgpath == null)
			return;
		int sx = (int)(scale*dim.getWidth());
		int sy = (int)(scale*dim.getHeight());
		int max = Math.max(sx, sy);
		bg = loadImage(bgpath).getScaledInstance(max, max, Image.SCALE_FAST);
		tracker.addImage(bg, 0);
		try {
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}

	public boolean inView(ROVector2f v, float r) {
		return inViewFrom(oA, v, r) || inViewFrom(oB, v, r);
	}

	public boolean inViewFrom(ROVector2f o, ROVector2f v, float r) {
		return isVisible(o, dim, v, r);
	}
}
