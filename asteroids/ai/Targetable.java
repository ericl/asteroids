package asteroids.ai;

import net.phys2d.math.ROVector2f;

public interface Targetable {
	public int getPointValue();
	public boolean canTarget();
	public float getRotation();
	public ROVector2f getPosition();
	public ROVector2f getVelocity();
}
