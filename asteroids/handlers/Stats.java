/**
 * Listens to game operations and prepares high scores for submission.
 */

package asteroids.handlers;

import java.io.*;

import java.net.*;

import java.security.*;

import java.util.*;

import asteroids.AbstractGame;
import asteroids.AbstractGame.Level;

import asteroids.ai.*;

import asteroids.bodies.*;

import net.phys2d.raw.*;

import static asteroids.AbstractGame.Level.*;

public class Stats {
	protected Vector<String> list = new Vector<String>();
	protected String lastChk;
	protected Field scenario;
	protected int hit = 0, att = 0, otherPoints = 0, myPoints = 0, finalScore = -1;
	protected boolean finalized;
	protected Entity myShip;
	protected double dmg = 0;

	/**
	 * Resets the game and the scores.
	 */
	public void reset(Field field) {
		scenario = field;
		lastChk = null;
		myShip = null;
		list = new Vector<String>();
		hit = att = myPoints = otherPoints = 0;
		finalized = false;
		finalScore = -1;
		dmg = 0;
		updateDifficulty();
	}

	public void setShip(Entity ship) {
		myShip = ship;
	}

	/**
	 * Records how many times one hits something.
	 */
	public void hit(Body body, CollisionEvent event) {
		if (finalized)
			return;
		hit++;
		dmg += Exploder.getDamage(event, body);
	}

	/**
	 * How many times fired by someone.
	 */
	public void fired(Body weap) {
		if (finalized)
			return;
		att++;
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
		if (AbstractGame.globalLevel.quantify() < d.quantify())
			AbstractGame.globalLevel = d;
	}

	private void updateDifficulty() {
		int score = score(), i = 100;
		if (score > 50*i)
			increaseLevel(DONE);
		else if (score > 40*i)
			increaseLevel(SWARM);
		else if (score > 27*i)
			increaseLevel(BLUE);
		else if (score > 15*i)
			increaseLevel(HARD);
		else if (score > 8*i)
			increaseLevel(MEDIUM);
		else if (score > 3*i)
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
	public void build(String name) {
		freezeScores();
		name = name.replace(" ", "%20");
		try {
			lastChk = md5(name + score() + hit + att
				+ (System.currentTimeMillis()/1000));
			URL init = new URL("http://a.cognoseed.org/post.php?scenario=0"
					+ "&name=" + name + "&score=" + score() + "&chk="
					+ lastChk);
			HttpURLConnection con = (HttpURLConnection)init.openConnection();
			con.connect();
			LineNumberReader content = new LineNumberReader(
				new InputStreamReader(con.getInputStream()));
			content.readLine();
			con.disconnect();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			readScores();
		}
	}
	
	/**
	 * Loads the latest list of high scores for get().
	 */
	protected void readScores() {
		List<String> output = list;
		output.clear();
		try {
			URL init = new URL("http://a.cognoseed.org/get.php?scenario=0");
			HttpURLConnection con = (HttpURLConnection)init.openConnection();
			con.connect();
			LineNumberReader content = new LineNumberReader(
				new InputStreamReader(con.getInputStream()));
			String s = content.readLine();
			while (s != null) {
				output.add(s);
				s = content.readLine();
			}
		} catch (Exception e) {
			if (output.isEmpty())
				output.add("");
			output.add(e.getClass().getName());
		}
	}

	/**
	 * Changes the last submitted high score to a new name.
	 * @param	name	The name to change to.
	 */
	public void edit(String name) {
		if (lastChk == null)
			return;
		name = name.replace(" ", "%20");
		try {
			URL init = new URL("http://a.cognoseed.org/edit.php?chk="
				+ lastChk + "&name=" + name);
			HttpURLConnection con = (HttpURLConnection)init.openConnection();
			con.connect();
			LineNumberReader content =
				new LineNumberReader(new InputStreamReader(con.getInputStream()));
			content.readLine();
			con.disconnect();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			readScores();
		}
	}

	/**
	 * @return	Hex string from md5 hashing.
	 */
	private static String md5(String hash) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] retHash = new byte[32];
		String str = "", proc;
		md.update(hash.getBytes(), 0, hash.length());
		retHash = md.digest();
		for (int i = 0; i < retHash.length; i++) {
			proc = Integer.toHexString(retHash[i] & 0xFF);
			if (proc.length() == 1)
				proc = "0" + proc;
			str += proc;
		}
		return str;
	}
}
