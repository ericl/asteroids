package asteroids;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import net.phys2d.raw.strategies.*;
import asteroids.bodies.*;
import asteroids.display.*;
import asteroids.handlers.*;
import static asteroids.Util.*;

public abstract class MPGame extends AbstractGame {
	protected JSplitPane jsplit;

	public MPGame(String title, int w, int h) {
		super(title, w*2, h);
		Canvas a, b;
		jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                           a = new Canvas(), b = new Canvas());
		a.setSize(w,h);
		b.setSize(w,h);
		jsplit.setSize(w*2, h);
		jsplit.setDividerLocation(.5);
		jsplit.setVisible(true);
		frame.add(jsplit);
	}

	public void init() {
		setDisplay(new MPDisplay(frame, jsplit));
		world.addListener(new Exploder(world, getDisplay()));
	}

	public MPDisplay getDisplay() {
		return (MPDisplay)super.getDisplay();
	}
}
