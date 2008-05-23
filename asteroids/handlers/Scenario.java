package asteroids.handlers;
import asteroids.bodies.*;
import asteroids.display.*;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import java.util.*;

// TODO: write abstract scenario instead
public interface Scenario {
	public void start();
	public void update();
	public boolean done();
	public int score();
}
