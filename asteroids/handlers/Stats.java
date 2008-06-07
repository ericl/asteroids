package asteroids.handlers;
import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import asteroids.handlers.Field;

public class Stats {
	private Vector<String> list = new Vector<String>();
	public int hit = 0, att = 0, kills = 0;
	public double dmg = 0.;

	public void reset() {
		list = new Vector<String>();
		hit = att = kills = 0;
		dmg = 0.;
	}

	public String get(int i) {
		try {
			return list.get(i);
		} catch (Exception e) {
			return "";
		}
	}

	public void build(String name, Field scenario) {
		List<String> output = list;
		scenario.score += kills*(hit/(double)att);
		if (!output.isEmpty())
				return;
		try {
			URL init = new URL("http://a.cognoseed.org/post.php?scenario="
					   + scenario.id + "&name=" + name + "&score=" +scenario.score() + "&chk="
					   + md5(name+scenario.id+scenario.score()+hit+att
						+(System.currentTimeMillis()/1000)));
			HttpURLConnection con = (HttpURLConnection)init.openConnection();
			con.connect();
			LineNumberReader content = new LineNumberReader(
				new InputStreamReader(con.getInputStream()));
			content.readLine();
			con.disconnect();
			init = new URL("http://a.cognoseed.org/get.php?scenario=" + scenario.id);
			con = (HttpURLConnection) init.openConnection();
			con.connect();
			content = new LineNumberReader(
				new InputStreamReader(con.getInputStream()));
			String s = content.readLine();
			while (s != null) {
				output.add(s);
				s = content.readLine();
			}
			con.disconnect();
		} catch (Exception e) {
			if (output.isEmpty())
				output.add("");
			output.add(e.getClass().getName());
			System.err.println(e);
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
