package asteroids.display;

/**
 * Manages frame rate by blocking when called and returning the approximate
 * time elapsed since the last frame (useful for calculating physics steps.)
 */
public class Timer {
	private float target_ns, frame_ns;
	private long old_ns, now_ns, old_diff, now_diff;
	private int sleep_ms;

	/**
	 * @param targetFPS The framerate this timer will try to keep.
	 */
	public Timer(float targetFPS) {
		target_ns = 1e9f / targetFPS;
		frame_ns = target_ns;
		old_ns = System.nanoTime();
		now_ns = old_ns;
		now_diff = old_diff = 0;
		sleep_ms = 0;
	}

	/**
	 * Step into the next frame.
	 * @return The average dt for the last few ticks.
	 */
	public float tick() {
		now_ns = System.nanoTime();
		now_diff = now_ns - old_ns;
		if (now_diff - old_diff > 10000000)
			sleep_ms = 0; // respond to sudden rendering activity
		else if (now_diff < target_ns)
			sleep_ms++;
		else
			sleep_ms--;
		if (sleep_ms < 0)
			sleep_ms = 0;
		old_diff = now_diff;
		try {
			// conserves cpu time when running at lower qualities
			Thread.sleep(sleep_ms);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		frame_ns = now_ns - old_ns;
		old_ns = now_ns;
		return frame_ns/1e9f;
	}
}
