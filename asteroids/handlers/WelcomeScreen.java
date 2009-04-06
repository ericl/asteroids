package asteroids.handlers;

import asteroids.display.*;
import asteroids.bodies.*;
import static asteroids.Util.*;

import net.phys2d.raw.*;
import net.phys2d.math.*;

public class WelcomeScreen extends Field {
	private int x;

	public WelcomeScreen(World w, Display d, int id, Ship ... ships) {
		super(w, d, id, ships);
	}

	public boolean done() {
		return false;
	}

	public ROVector2f getCenter() {
		return v(0,x--);
	}

	public String toString() {
		return "";
	}
}
