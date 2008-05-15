package asteroids.display;

/**
 * Manages frame rate by blocking when called and returning the approximate
 * time elapsed since the last frame (useful for calculating physics steps.)
 */
public class Timer {
	private float target_ns, frame_ns;
	private long old_ns, now_ns;
	private double sleep_ms;

	/**
	 * @param targetFPS The framerate this timer will try to keep.
	 */
	public Timer(float targetFPS) {
		target_ns = 1e9f / targetFPS;
		frame_ns = target_ns;
		old_ns = System.nanoTime();
		now_ns = old_ns;
		sleep_ms = 0;
	}

	/**
	 * Step into the next frame.
	 * @return The average dt for the last few ticks.
	 */
	public float tick() {
		now_ns = System.nanoTime();
		sleep_ms += now_ns - old_ns > target_ns ? -1 : 1;
		sleep_ms = sleep_ms > 0 ? sleep_ms : 0;
		try {
			Thread.sleep((int)sleep_ms);
		} catch (Exception e) {
			System.err.println(e);
		}
		frame_ns = now_ns - old_ns;
		old_ns = now_ns;
		return frame_ns/1e9f;
	}
}
