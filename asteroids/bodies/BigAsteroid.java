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

package asteroids.bodies;
import java.util.*;
import net.phys2d.raw.Body;
import net.phys2d.math.ROVector2f;
import static asteroids.Util.*;

public class BigAsteroid extends TexturedAsteroid {

	private static ROVector2f[] raw = {v(97,1), v(108,3), v(113,8), v(149,14), v(149,18), v(156,23), v(160,28), v(177,35), v(184,53), v(180,58), v(189,72),
		v(189,79), v(186,82), v(188,89), v(188,101), v(179,109), v(181,119), v(169,131), v(169,136), v(163,141), v(157,152), v(157,158), v(131,166), v(93,168),
		v(72,164), v(49,152), v(16,121), v(15,112), v(11,100), v(14,100), v(11,100), v(16,89), v(11,75), v(22,56), v(22,51), v(32,38), v(27,27), v(37,17)};
	
	public BigAsteroid(float size) {
		super(raw, "pixmaps/1.png", 90, size);
	}

	public Body getRemnant() {
		return getRadius() > 15 ? new MediumAsteroid(getRadius() / 2) : null;
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(6);
		SmallAsteroid tmp;
		if (getRadius() > 10)
			for (int i=0; i < 6; i++) {
				tmp = new SmallAsteroid(getRadius() / 3);
				f.add(tmp);
			}
		powerup(f);
		return f;
	}
}
