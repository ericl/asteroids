package asteroids.handlers;

import asteroids.display.*;
import asteroids.bodies.*;
import static asteroids.Util.*;

import net.phys2d.raw.*;
import net.phys2d.math.*;

public class WelcomeScreen extends Field {
	private long init = Timer.gameTime();

	public WelcomeScreen(World w, Display d, Entity ... ships) {
		super(w, d, "hi", ships);
	}

	public boolean done() {
		return false;
	}

	public ROVector2f getCenter() {
		return v(0, ((double)Timer.gameTime() - init)/-15);
	}

	public String toString() {
		return "";
	}
}
