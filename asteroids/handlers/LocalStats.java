/*
 * Asteroids - APCS Final Project
 *
 * This source is provided under the terms of the BSD License.
 *
 * Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The authors' names may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

/**
 * Provides high score list on local machine.
 */
public class LocalStats extends Stats {
	private static HighScore record;
	static {
		File file = new File(System.getProperty("user.home") + "/.asteroids-hs");
		if (file.exists()) {
			try {
				record = (HighScore)(new ObjectInputStream(
					new FileInputStream(file)
				).readObject());
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		if (record == null)
			record = new HighScore();
	}

	private static void commit() {
		try {
			new ObjectOutputStream(
				new FileOutputStream(
					new File(System.getProperty("user.home") + "/.asteroids-hs")
				)
			).writeObject(record);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private static class HighScore implements java.io.Serializable {
		private class Score implements Comparable<Score>, java.io.Serializable {
			public final static long serialVersionUID = 134987263492L;
			public String name;
			public final int score;

			public Score(String n, int s) {
				name = n;
				score = s;
			}

			public int compareTo(Score other) {
				return other.score - this.score;
			}

			public String toString() {
				return this.name + "     " + this.score;
			}
		}

		public final static long serialVersionUID = 9327419832749823L;
		private Map<String,Score> scores = new HashMap<String,Score>();
		private List<Score> ranks = new ArrayList<Score>();

		public void submit(String id, String name, int score) {
			Score obj = new Score(name, score);
			scores.put(id, obj);
			ranks.add(obj);
			Collections.sort(ranks);
			if (ranks.size() > 10)
				ranks.remove(ranks.size() - 1);
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

		public String toString() {
			return ranks.toString();
		}
	}

	public String get(int i) {
		return record.get(i);
	}

	/**
	 * Sends the current score to the high score tables.
	 * @param	name	Name of user.
	 */
	public void build(String name) {
		freezeScores();
		lastChk = "TIMESTAMP=" + System.currentTimeMillis();
		record.submit(lastChk, name, score());
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
