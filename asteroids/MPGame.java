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

package asteroids;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import asteroids.display.*;

public abstract class MPGame extends AbstractGame {
	protected JSplitPane jsplit;
	protected MPDisplay display;

	public MPGame(String title, Dimension dim) {
		super(title, dim);
		display = (MPDisplay)super.display;
	}

	protected Display makeDisplay() {
		frame.setLocationByPlatform(true);
		// redirect canvas keyevents to the frame
		final KeyboardFocusManager manager =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getSource() == frame)
					return false;
				manager.redispatchEvent(frame, e);
				return true;
			}
		});
		Canvas a, b;
		jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		         a = new Canvas(), b = new Canvas());
		a.setSize(dim);
		b.setSize(dim);
		a.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		b.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		jsplit.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		frame.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		jsplit.setDividerLocation(.5);
		jsplit.setVisible(true);
		frame.setContentPane(jsplit);
		return new MPDisplay(frame, jsplit, dim);
	}

	public void pause() {
		if (!pause) {
			pause = true;
			synchronized (display) {
				for (Graphics2D g2d : display.getAllGraphics()) {
					g2d.setColor(new Color(100,100,100,100));
					g2d.fillRect(0,0,display.w(0),display.h(0));
					g2d.setFont(new Font("SanSerif", Font.BOLD, 15));
					g2d.setColor(Color.RED);
					g2d.drawString("PAUSED",20,display.h(-45));
				}
				display.show();
			}
		}
	}
}
