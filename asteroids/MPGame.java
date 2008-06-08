package asteroids;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
