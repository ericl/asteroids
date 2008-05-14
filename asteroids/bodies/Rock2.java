package asteroids.bodies;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class Rock2 extends TexturedAsteroid {
	private static ROVector2f[] raw = {v(38,4),v(46,9),v(60,19),v(72,32),v(82,41),v(82,53),v(87,60),v(87,72),v(82,79),v(72,88),v(60,88),v(49,79),v(38,79),v(20,58),v(19,53),v(15,41),v(12,25),v(12,19),v(19,10),v(36,4)};

	public Rock2(float radius) {
		super(raw, "pixmaps/rock2.png", 40, radius);
	}
}
