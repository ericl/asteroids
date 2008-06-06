package asteroids.handlers;
import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;

public class Stats {
	private Map<String, Integer> kill = new HashMap<String, Integer>();
	private Map<String, Double> dmg = new HashMap<String, Double>();
	private Vector<String> list = new Vector<String>();
	public int hit = 0, att = 0;

	public void reset() {
		list = new Vector<String>();
		kill.clear();
		dmg.clear();
		list.clear();
		hit = att = 0;
	}

	public String get(int i) {
		try {
			return list.get(i);
		} catch (Exception e) {
			return "";
		}
	}

	public void build(int scenario, String name, int score) {
		List<String> output = list;
		if (!output.isEmpty())
				return;
		try {
			URL init = new URL("http://a.cognoseed.org/post.php?scenario="
				+ scenario + "&name=" + name + "&score=" + score + "&chk="
				+ md5(name+scenario+score+hit+att
						+(System.currentTimeMillis()/1000)));
			HttpURLConnection con = (HttpURLConnection)init.openConnection();
			con.connect();
			LineNumberReader content = new LineNumberReader(
				new InputStreamReader(con.getInputStream()));
			content.readLine();
			con.disconnect();
			init = new URL("http://a.cognoseed.org/get.php?scenario=" + scenario);
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
			System.err.println(e);
		}
	}

	public void kill(String body) {
		body = body.substring(body.lastIndexOf('.')+1);
		Integer i = kill.get(body);
		if (i == null)
			i = 0;
		kill.put(body, ++i);
	}

	public void dmg(String body, double amt) {
		body = body.substring(body.lastIndexOf('.')+1);
		Double d = dmg.get(body);
		if (d == null)
			d = 0.;
		dmg.put(body, d+amt);
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
