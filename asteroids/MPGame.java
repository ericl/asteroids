package asteroids;
import javax.swing.*;
import java.awt.*;
import asteroids.display.*;
import asteroids.handlers.Pauser;

public abstract class MPGame extends AbstractGame {
	protected JSplitPane jsplit;
	protected MPDisplay display;

	public MPGame(String title, Dimension dim) {
		super(title, dim);
		display = (MPDisplay)super.display;
	}	

	protected Display makeDisplay() {
		Canvas a, b;
		jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		         a = new Canvas(), b = new Canvas());
		focus.add(a);
		focus.add(b);
		a.addFocusListener(focus);
		b.addFocusListener(focus);
		a.setSize(dim);
		b.setSize(dim);
		a.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		b.setMinimumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		jsplit.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		frame.setSize(new Dimension((int)dim.getWidth()*2,(int)dim.getHeight()));
		jsplit.setDividerLocation(.5);
		jsplit.setVisible(true);
		frame.add(jsplit);
		return new MPDisplay(frame, jsplit, dim);
	}
}
