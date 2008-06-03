package asteroids.handlers;
import java.io.*;
import java.net.*;
import java.security.*;

public class Session {
	private String session;
	private boolean valid;

	public Session(String user, String pass) {
		long time = System.currentTimeMillis() / 1000;
		try {
			String token = md5(md5(pass) + time);
			URL init = new URL("http://a.cognoseed.org/init.php?u=" + user + "&t=" + time + "&a=" + token);
			HttpURLConnection connection = (HttpURLConnection) init.openConnection();
			connection.connect();
			BufferedReader content = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			session = content.readLine(); // Should check the validity of the session after disconnecting.
			connection.disconnect();
			valid = true;
		} catch (Exception e) {
			valid = false;
		}
	}

	public boolean isValid() {
		return valid;
	}

	private static String md5(String hash) throws Exception {
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
