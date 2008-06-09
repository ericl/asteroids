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

package asteroids.weapons;
import java.util.List;
import static asteroids.Util.*;
import net.phys2d.raw.*;
import net.phys2d.math.*;
import net.phys2d.raw.shapes.*;

public class Missile extends Weapon {
	protected Body myTarget;
	
	// to be deleted by will
	private static float myRadius = 2;
	/////
	
	public Missile() {
		super(new Circle(myRadius), 1);
	}
	
	public Vector2f getTextureCenter() {
		return v(7,3);
	}
	
	public boolean canExplode() {
		return true;
	}
	
	public float getDamage() {
		return .1f;
	}
	
	public float getReloadTime() {
		return 300;
	}
	
	public float getSpeed() {
		return 75;
	}
	
	public Body getRemnant() {
		return new LaserExplosion();
	}
	
	public List<Body> getFragments() {
		return null;
	}
	
	public String getTexturePath() {
		return "pixmaps/laser.png";
	}

	public float getTextureScaleFactor() {
		return 2;
	}	
	
	public float getRadius() {
		return 2;
	}
	
	public void setTarget(Body b) {
		myTarget = b;
	}
	
	public Body getTarget() {
		return myTarget;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see net.phys2d.raw.Body#endFrame()
	 * blah blah blah... ^ eclipse
	 * 
	 * problem exists below...
	 * when commented/removed, missiles display on screen fine and act like lasers
	 * when included, missiles magicly disappear
	 * 
	 * 
	 * todo/fix on this to get missile working...
	 * fix the force applied...missile is currently not capable of turning much
	 */
	public void endFrame() {
		if(getTarget() != null) {
//			System.out.println("target locked");
			
			// get the target's velocity
			Vector2f vec = new Vector2f(getTarget().getVelocity());
			
			// now add it to the missiles velocity (vectors)
			vec.add(getVelocity());
			
			// get the difference in the missiles velocity and target velocity
			float xd = getVelocity().getX() - getTarget().getVelocity().getX();
			float yd = getVelocity().getY() - getTarget().getVelocity().getY();
			

			//scale down a bit
			vec.scale(0.4f);
			
			// adjust the velocity of the missile
			adjustVelocity(new Vector2f(xd * 0.02f, yd * 0.02f));
			
			// scale like crazy for force then control velocity
			vec.scale(9);
			
			// add a force in same direction(after all, missiles do accelerate);
			addForce(vec);
			
			// prep vec for controlling velocity
			vec.scale(0.005f);
			
			// now control it
			adjustVelocity(new Vector2f(vec.getX(), vec.getY()));
		}
	}
	
}
