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
import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import net.phys2d.raw.*;

public class Stats {
	private Vector<String> list = new Vector<String>();
	private String lastChk;
	private Field scenario;
	private int hit = 0, att = 0, kills = 0;
	private boolean finalized;
	private double dmg = 0;
	private int finalScore = -1;

	public void reset(Field field) {
		scenario = field;
		lastChk = null;
		list = new Vector<String>();
		hit = att = kills = 0;
		finalized = false;
		finalScore = -1;
		dmg = 0;
	}

	public void hit(Body body, CollisionEvent event) {
		if (finalized)
			return;
		hit++;
		dmg += Exploder.getDamage(event, body);
	}

	public void fired(Body weap) {
		if (finalized)
			return;
		att++;
	}

	public void kill(Body body, CollisionEvent event) {
		if (finalized)
			return;
		kills++;
	}

	public String get(int i) {
		try {
			return list.get(i);
		} catch (Exception e) {
			return "";
		}
	}

	public int score() {
		if (finalized)
			return finalScore;
		else
			return scenario.asteroids() +
				(att > 0 ? (int)(5*kills*(hit/(double)att)) : (int)(.5*scenario.asteroids()));
	}

	private void freezeScores() {
		if (finalized)
			return;
		finalScore = score();
		finalized = true;
	}

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
	
	public void readScores() {
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
