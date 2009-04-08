package asteroids.ai;

import asteroids.handlers.*;
import asteroids.bodies.*;

public interface Entity extends Targetable, Automated {
	public boolean dead();
	public void reset();
	public int numMissiles();
	public boolean isInvincible();
	public int numDeaths();
	public void addStatsListener(Stats s);
	public void setShield(Shield shield);
	public double shieldInfo();
}
