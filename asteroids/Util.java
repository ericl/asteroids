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

package asteroids;
import java.awt.*;
import net.phys2d.math.*;

public class Util {
	private Util() {
		// prevent construction
	}

	public static Vector2f v(Number x, Number y) {
		return new Vector2f(x.floatValue(), y.floatValue());
	}

	public static Dimension d(Number x, Number y) {
		return new Dimension(x.intValue(), y.intValue());
	}

	public static Vector2f v(Dimension d) {
		return v(d.getWidth(), d.getHeight());
	}

	public static Dimension d(Vector2f d) {
		return d(d.getX(), d.getY());
	}

	public static float range(Number minR, Number maxR) {
		float min = minR.floatValue();
		float max = maxR.floatValue();
		return (float)(min+(max-min)*Math.random());
	}

	public static Vector2f direction(Number rotation) {
		return v(Math.sin(rotation.doubleValue()),
		        -Math.cos(rotation.doubleValue()));
	}

	public static boolean oneIn(int num) {
		return num*Math.random() < 1;
	}

	public static Color randomColor() {
		return new Color((int)range(1,255),(int)range(1,255),(int)range(1,255));
	}
}
