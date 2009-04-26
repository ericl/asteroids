package asteroids.ai;

import net.phys2d.math.ROVector2f;

import asteroids.bodies.*;

public interface Targetable extends Explodable {
	public int getPointValue();
	public boolean isVisible();
	public boolean targetableBy(Object o);
	public float getRotation();
	public ROVector2f getPosition();
	public ROVector2f getVelocity();
}
