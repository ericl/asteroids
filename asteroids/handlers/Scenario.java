package asteroids.handlers;

// TODO: write different scenarios
// (right now Field handles everything, making this pointless)
public interface Scenario {
	public void start();
	public void update();
	public boolean done();
	public int score();
}
