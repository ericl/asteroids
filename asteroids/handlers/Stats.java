package asteroids.handlers;
import java.util.*;

public class Stats {
	private HashMap<String, Long> kill = new HashMap<String, Long>();
	private HashMap<String, Float> dmg = new HashMap<String, Float>();
	//private String scenario;

	/*public Stats(String scenario) {
		this.scenario = scenario;
	}*/

	public void reset() {
		kill = new HashMap<String, Long>();
		dmg = new HashMap<String, Float>();
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
		if (kill.containsKey(body))
			kill.put(body, kill.get(body)+1);
		else
			kill.put(body, 1l);
	}

	public void dmg(String body, double amt) {
		if (dmg.containsKey(body))
			dmg.put(body, dmg.get(body)+(float)amt);
		else
			dmg.put(body, (float)amt);
	}
}
