package asteroids.ai;

import asteroids.handlers.*;

public interface Entity extends Targetable, Automated {
	public boolean dead();
	public void reset();
	public int numMissiles();
	public boolean isInvincible();
	public int numDeaths();
	public void addStatsListener(Stats s);
}
