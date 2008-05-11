import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;
import java.awt.Color;

public class TestAsteroid extends Asteroid implements Textured {

	private TestAsteroid(DynamicShape s, float m) {
		super(s, m);
	}

	public Vector2f getTextureCenter() {
		return new Vector2f(50,50);
	}

	public String getTexturePath() {
		return "pixmaps/test.png";
	}

	public float getTextureScaleFactor() {
		return 0.5f;
	}

	public float getRadius() {
		return 36;
	}

	public static TestAsteroid getInstance() {
		return new TestAsteroid(new Box(50,50), 1000);
	}
}
