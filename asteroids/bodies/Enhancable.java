package asteroids.bodies;

public interface Enhancable {
	public void setHealth(float health);
	public void addMissiles(int num);
	public void gainInvincibility(int time, int warn);
	public void gainBeams(int beams);
	public void upgradeWeapons();
	public void raiseShields();
}
