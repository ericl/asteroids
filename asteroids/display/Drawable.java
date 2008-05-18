/**
 * Drawable object. This actually breaks the whole point of the
 * Display interface since it relies on Graphics2D....
 */

package asteroids.display;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;
import java.awt.Graphics2D;

public interface Drawable extends Visible {

	public void drawTo(Graphics2D g2d, float xo, float yo);

	public ROVector2f getPosition();
}
