/**
 * Listens to game operations and prepares high scores for submission.
 */

package asteroids.handlers;

import java.util.*;

import asteroids.AbstractGame.Level;

import asteroids.AbstractGame;

import asteroids.ai.*;

import asteroids.bodies.*;

import asteroids.weapons.LevelUp;

import net.phys2d.raw.*;

import static asteroids.AbstractGame.Level.*;
import static asteroids.bodies.Invincibility.*;

public abstract class Stats {
	protected Vector<String> list = new Vector<String>();
	protected String lastChk;
	protected Field scenario;
	protected int otherPoints = 0, myPoints = 0, finalScore = -1;
	protected boolean finalized;
	protected Entity myShip;

	/**
	 * Resets the game and the scores.
	 */
	public void reset(Field field) {
		scenario = field;
		lastChk = null;
		myShip = null;
		list = new Vector<String>();
		myPoints = otherPoints = 0;
		finalized = false;
		finalScore = -1;
		updateDifficulty();
	}

	public void setShip(Entity ship) {
		myShip = ship;
	}

	/**
	 * How many times someone has killed.
	 */
	public void kill(Object killer, Body victim, CollisionEvent event) {
		if (finalized)
			return;
		if (victim instanceof Targetable) {
			Targetable t = (Targetable)victim;
			if (killer == myShip)
				myPoints += t.getPointValue();
			else
				otherPoints += t.getPointValue();
		}
		updateDifficulty();
	}

	private void increaseLevel(Level d) {
		if (AbstractGame.globalLevel.quantify() < d.quantify()) {
			AbstractGame.globalLevel = d;
			myShip.setHealth(1);
			myShip.gainInvincibility(WARNING_TIME, WARNING_TIME);
			LevelUp e = new LevelUp(myShip.getWorld());
			e.setPosition(myShip.getPosition().getX(), myShip.getPosition().getY());
			e.setTracking(myShip, myShip);
			myShip.getWorld().add(e);
		}
	}

	private void updateDifficulty() {
		int score = score(), i = 100;
		if (score > 56*i) // 20 heavy rocks
			increaseLevel(DONE);
		else if (score > 46*i) // 5 blue terrors
			increaseLevel(SWARM);
		else if (score > 36*i) // 7 shielded juggernauts
			increaseLevel(BLUE);
		else if (score > 15*i) // 9 [shielded] ships
			increaseLevel(HARD);
		else if (score > 6*i) // 10 shielded frigates
			increaseLevel(MEDIUM);
		else if (score > 3*i) // 10 frigates
			increaseLevel(EASY);
	}

	public String get(int i) {
		try {
			return list.get(i);
		} catch (Exception e) {
			return "";
		}
	}

	public String getCause(int i) {
		return "(unimplemented)";
	}

	/**
	 * @return	The current score of the scenario.
	 */
	public int score() {
		if (finalized)
			return finalScore;
		else
			return otherPoints / 5 + myPoints + (scenario == null ? 0 : scenario.asteroids() / 10);
	}

	/**
	 * Stops the scores from getting any higher.
	 * Prevents score inconsistencies / race conditions.
	 */
	public void freezeScores() {
		if (finalized)
			return;
		finalScore = score();
		finalized = true;
	}
	
	/**
	 * Sends the current score to the high score tables.
	 * @param	name	Name of user.
	 */
	public abstract void build(String name);
	
	/**
	 * Changes the last submitted high score to a new name.
	 * @param	name	The name to change to.
	 */
	public abstract void edit(String name);
}
