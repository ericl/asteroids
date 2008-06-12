/*
 * Asteroids - APCS Final Project
 *
 * This source is provided under the terms of the BSD License.
 *
 * Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The authors' names may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package asteroids.display;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import net.phys2d.math.*;
import static net.phys2d.math.MathUtil.*;
import static asteroids.Util.*;

/**
 * A single-player implementation of the display.
 */
public class BasicDisplay extends Display {
	protected BufferStrategy strategy;
	protected Graphics2D buf;
	protected Vector2f origin = v(0,0);
	protected double scale = 1;
	protected long resizetime = Long.MAX_VALUE;
	protected String bgpath;
	protected Image bg;

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
				// allow user to get wider area of view
				// this should be tracked in the high scores
//				scale = Math.min(sx, sy);
				dim.setSize(ORIGINAL_WIDTH*sx/scale,ORIGINAL_HEIGHT*sy/scale);
				resizetime = System.currentTimeMillis();
			}
		});
	}

	/**
	 * @param	center	The vector x*y representing the center of the display.
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
			Image i = loadImage(thing.getTexturePath());
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
		buf.setRenderingHint(RenderingHints.KEY_RENDERING,
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
		try { // bg flickering is especially noticable
			tracker.waitForID(0);
		} catch (Exception e) {
			System.err.println("E: what?");
		}
	}
}
