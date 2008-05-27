package asteroids.handlers;
import java.util.HashSet;
import java.awt.Component;
import java.awt.event.*;
import asteroids.AbstractGame;

public class Pauser extends FocusAdapter {
	private AbstractGame g;
	private HashSet<Component> s = new HashSet<Component>();

	public Pauser(Component c, AbstractGame g) {
		this.g = g;
		s.add(c);
	}

	public void focusGained(FocusEvent e) {
		g.unpause();
	}

	public void focusLost(FocusEvent e) {
		if (!s.contains(e.getOppositeComponent()))
			g.pause();
	}

	public void add(Component c) {
		s.add(c);
	}
}

	
