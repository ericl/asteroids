package asteroids.handlers;
import java.util.concurrent.*;
import java.util.*;

public class Stats {
	private Map<String, Long> kill = new ConcurrentHashMap<String, Long>();
	private Map<String, Float> dmg = new ConcurrentHashMap<String, Float>();
//	private String scenario;
//
//	public Stats(String scenario) {
//		this.scenario = scenario;
//	}

	public void reset() {
		kill.clear();
		dmg.clear();
	}

	public void print() {
		System.out.println("Kills: ");
		for (String body : kill.keySet())
			System.out.println("\t" + body + ": " + kill.get(body));
		System.out.println("Damage: ");
		for (String body : dmg.keySet())
			System.out.println("\t" + body + ": " + dmg.get(body));
	}

	public void kill(String body) {
		body = body.substring(body.lastIndexOf('.')+1);
		Long l = kill.get(body);
		if (l == null)
			 l = 0l;
		kill.put(body, ++l);
	}

	public void dmg(String body, float amt) {
		body = body.substring(body.lastIndexOf('.')+1);
		Float f = dmg.get(body);
		if (f == null)
			 f = 0f;
		dmg.put(body, f+amt);
	}
}
