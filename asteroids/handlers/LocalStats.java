/**
 * Provides high score list on local machine.
 */

package asteroids.handlers;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.*;
import java.util.Collections;

import static asteroids.Util.*;

public class LocalStats extends Stats {
	private static HighScore record;
	private static File hsFile = mktemp(".asteroids-hs");
	static {
		if (hsFile.exists()) {
			try {
				record = (HighScore)(new ObjectInputStream(
					new FileInputStream(hsFile)
				).readObject());
			} catch (Exception e) {}
		}
		if (record == null)
			record = new HighScore();
	}

	private static void commit() {
		try {
			File swp = new File(hsFile.getPath() + ".swp");
			new ObjectOutputStream(new FileOutputStream(swp)).writeObject(record);
			swp.renameTo(hsFile);
		} catch (IOException e) {
			System.err.println("on highscore commit: " + e);
		}
	}

	private static class HighScore implements java.io.Serializable {
		private class Score implements Comparable<Score>, java.io.Serializable {
			public final static long serialVersionUID = 134987263492L;
			public String name;
			public String killer;
			public final int score;

			public Score(String n, String k, int s) {
				name = n;
				killer = k;
				score = s;
			}

			public int compareTo(Score other) {
				return other.score - this.score;
			}

			public String toString() {
				return this.name + "     " + this.score;
			}

			public String getCause() {
				return killer;
			}
		}

		public final static long serialVersionUID = 9327419832749823L;
		private Map<String,Score> scores = new HashMap<String,Score>();
		private List<Score> ranks = new ArrayList<Score>();

		private void gc() {
			if (ranks.size() > 10)
				ranks.remove(ranks.size() - 1);
			Map<String,Score> tmp = new HashMap<String,Score>();
			for (String key : scores.keySet())
				if (ranks.contains(scores.get(key)))
					tmp.put(key, scores.get(key));
			scores = tmp;
		}

		public void submit(String id, String name, String killer, int score) {
			Score obj = new Score(name, killer, score);
			scores.put(id, obj);
			ranks.add(obj);
			Collections.sort(ranks);
			gc();
			commit();
		}

		public void edit(String id, String name) {
			Score obj = scores.get(id);
			if (obj != null)
				obj.name = name;
			commit();
		}

		public String get(int rank) {
			try {
				return ranks.get(rank - 1).toString();
			} catch (Exception e) {
				return "";
			}
		}

		public String getCause(int rank) {
			try {
				return ranks.get(rank - 1).getCause();
			} catch (Exception e) {
				return "";
			}
		}

		public String toString() {
			return ranks.toString();
		}
	}

	public String get(int i) {
		return record.get(i);
	}

	public String getCause(int i) {
		return record.getCause(i);
	}

	/**
	 * Sends the current score to the high score tables.
	 * @param	name	Name of user.
	 */
	public void build(String name) {
		freezeScores();
		lastChk = "TIMESTAMP=" + System.currentTimeMillis();
		record.submit(lastChk, name, myShip.killer(), score());
	}

	/**
	 * Changes the last submitted high score to a new name.
	 * @param	name	The name to change to.
	 */
	public void edit(String name) {
		if (lastChk == null)
			return;
		record.edit(lastChk, name);
	}
}
