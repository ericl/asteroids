package asteroids.bodies;

import net.phys2d.raw.*;

public interface Enhancable {
	public void multiplyHealth(float health);
	public void setHealth(float health);
	public void addMissiles(int num);
	public void gainInvincibility(int time, int warn);
	public void upgradeWeapons();
	public void raiseShields();
	public void setKiller(Body b);
}
