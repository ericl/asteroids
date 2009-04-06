/**
 * Manages frame rate by blocking when called and returning the approximate
 * time elapsed since the last frame (useful for calculating physics steps.)
 */

package asteroids.handlers;

public class Timer {
	private static long pauseTime, lastTime;
	private final float target_ns;
	private long old_ns, now_ns, old_diff, now_diff, sleep_ms;

	/**
	 * @return	Time from the game's perspective in milliseconds.
	 */
	public static long gameTime() {
		return System.currentTimeMillis() - pauseTime;
	}

	/**
	 * @param	targetFPS	The framerate this timer will try to keep.
	 */
	public Timer(float targetFPS) {
		target_ns = 1e9f / targetFPS;
		lastTime = System.currentTimeMillis();
		reset();
	}

	/**
	 * Disregard the last segment of time.
	 * The time delta is used to calculate gameTime.
	 */
	public void reset() {
		pauseTime += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		old_ns = System.nanoTime();
		now_ns = old_ns;
		now_diff = old_diff = 0;
		sleep_ms = 0;
	}

	/**
	 * Step into the next frame.
	 * @return	The approximate dt in seconds for the last tick.
	 */
	public float tick() {
		now_ns = System.nanoTime();
		now_diff = now_ns - old_ns;
		if (now_diff - old_diff > 1e7)
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
			e.printStackTrace();
		}
		float frame_ns = now_ns - old_ns;
		old_ns = now_ns;
		lastTime = System.currentTimeMillis();
		return frame_ns/1e9f;
	}
}
