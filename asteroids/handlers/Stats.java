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
	Long i = kill.get(body);
	if (i != null)
	    i = i + 1;
	else
	    i = new Long(1);
	kill.put(body, i);
    }

    public void dmg(String body, double amt) {
	Float f = dmg.get(body);
	if (f != null)
	    f = f + (float) amt;
	else
	    f = (float) amt;
	dmg.put(body, f);
    }
}