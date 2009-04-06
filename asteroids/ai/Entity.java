package asteroids.ai;

public interface Entity extends Targetable, Automated {
	public boolean dead();
	public void reset();
	public int numMissiles();
	public boolean isInvincible();
	public int numDeaths();
}
