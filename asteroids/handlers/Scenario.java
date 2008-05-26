package asteroids.handlers;

// TODO: write abstract scenario instead
public interface Scenario {
	public void start();
	public void update();
	public boolean done();
	public int score();
}
