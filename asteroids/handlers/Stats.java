/**
 * Listens to game operations and prepares high scores for submission.
 */

package asteroids.handlers;
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import net.phys2d.raw.*;
import asteroids.bodies.*;
import asteroids.ai.*;

public class Stats {
	protected Vector<String> list = new Vector<String>();
	protected String lastChk;
	protected Field scenario;
	protected int hit = 0, att = 0, otherPoints = 0, myPoints = 0, finalScore = -1;
	protected boolean finalized;
	protected Ship myShip;
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
	}

	public void setShip(Ship ship) {
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
	public void kill(Body killer, Body victim, CollisionEvent event) {
		if (finalized)
			return;
		if (victim instanceof Targetable) {
			Targetable t = (Targetable)victim;
			if (killer == myShip) {
				myPoints += t.getPointValue();
			} else {
				otherPoints += t.getPointValue();
			}
		}
	}

	public String get(int i) {
		try {
			return list.get(i);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @return	The current score of the scenario.
	 */
	public int score() {
		if (finalized)
			return finalScore;
		else
			return otherPoints / 10 + myPoints + scenario.asteroids() / 10;
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
			lastChk = md5(name + scenario.getID() + score() + hit + att
				+ (System.currentTimeMillis()/1000));
			URL init = new URL("http://a.cognoseed.org/post.php?scenario="
					+ scenario.getID() + "&name=" + name + "&score=" + score() + "&chk="
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
			URL init = new URL("http://a.cognoseed.org/get.php?scenario="
				+ scenario.getID());
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
