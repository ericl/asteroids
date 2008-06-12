/*
 * Asteroids - APCS Final Project
 *
 * This source is provided under the terms of the BSD License.
 *
 * Copyright (c) 2008, Evan Hang, William Ho, Eric Liang, Sean Webster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The authors' names may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package asteroids.handlers;

/**
 * Manages frame rate by blocking when called and returning the approximate
 * time elapsed since the last frame (useful for calculating physics steps.)
 */
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
