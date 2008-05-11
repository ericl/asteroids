/**
 * Drawable object. This actually breaks the whole point of the
 * Display interface since it relies on Graphics2D....
 */
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;

public interface Drawable {

	public void drawTo(Graphics2D g2d, float xo, float yo);

	/**
	 * Maximum visible radius of the body when drawn.
	 */
	public float getRadius();

	public ROVector2f getPosition();
}
