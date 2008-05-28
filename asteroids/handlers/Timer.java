package asteroids.handlers;

/**
 * Manages frame rate by blocking when called and returning the approximate
 * time elapsed since the last frame (useful for calculating physics steps.)
 */
public class Timer {
	private final float target_ns;
	private long old_ns, now_ns, old_diff, now_diff, sleep_ms;

	/**
	 * @param targetFPS The framerate this timer will try to keep.
	 */
	public Timer(float targetFPS) {
		target_ns = 1e9f / targetFPS;
		reset();
	}

	/**
	 * Reset the timer, as if newly constructed.
	 */
	public void reset() {
		old_ns = System.nanoTime();
		now_ns = old_ns;
		now_diff = old_diff = 0;
		sleep_ms = 0;
	}

	/**
	 * Step into the next frame.
	 * @return The approximate dt in seconds for the last tick.
	 */
	public float tick() {
		now_ns = System.nanoTime();
		now_diff = now_ns - old_ns;
		if (now_diff - old_diff > 10000000)
			sleep_ms = 0; // respond to sudden rendering activity
		else if (now_diff < target_ns)
			sleep_ms++;
		else if (sleep_ms > 0)
			sleep_ms--;
		old_diff = now_diff;
		try {
			// conserves cpu time when running at lower qualities
			Thread.sleep(sleep_ms);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		float frame_ns = now_ns - old_ns;
		old_ns = now_ns;
		return frame_ns/1e9f;
	}
}
