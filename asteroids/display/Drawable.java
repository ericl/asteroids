/**
 * Drawable object.
 */

package asteroids.display;
import java.awt.Graphics2D;
import net.phys2d.math.*;

public interface Drawable extends Visible {
	public void drawTo(Graphics2D g2d, ROVector2f o);
}
