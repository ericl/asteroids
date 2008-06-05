package asteroids.handlers;
import java.util.*;

public class Stats {
	// necessary?
	private Map<String, Long> kill = new HashMap<String, Long>();
	private Map<String, Float> dmg = new HashMap<String, Float>();
	public int hit = 0, att = 0;
//	private String scenario;
//
//	public Stats(String scenario) {
//		this.scenario = scenario;
//	}

	public void reset() {
		kill.clear();
		dmg.clear();
		hit = att = 0;
	}

	public void print() {
		if (att == 0) return;
		System.out.println("Attempts: " + att
					+ "\nHits: " + hit
					+ "\nAccuracy: "+(int)(((double)hit/att)*100)+"%");
		if (!kill.isEmpty()) {
			System.out.println("Kills: ");
			for (String body : kill.keySet())
				System.out.println("\t" + body + ": " + kill.get(body));
		}
		if (dmg.isEmpty()) return;
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
