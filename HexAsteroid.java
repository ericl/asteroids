import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class HexAsteroid extends PolyAsteroid {
	// kinda like a smashed hexagon
	private static ROVector2f[] geo = {new Vector2f(-30,0), new Vector2f(-10,-10),new Vector2f(10,-10), new Vector2f(30,0), new Vector2f(10,20), new Vector2f(-10,20)};

	protected HexAsteroid(Polygon poly) {
		super(poly);
	}

	public float getRadius() {
		return 30;
	}

	public static HexAsteroid getInstance() {
		return new HexAsteroid(new Polygon(geo));
	}
}
