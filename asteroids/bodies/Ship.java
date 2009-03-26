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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.*;
import net.phys2d.math.*;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.*;
import asteroids.*;
import asteroids.display.*;
import asteroids.weapons.*;
import asteroids.handlers.*;
import asteroids.handlers.Timer;
import static asteroids.Util.*;

/**
 * User-controlled ship in the world.
 */
public class Ship extends Body
		implements Drawable, Textured, Explodable, KeyListener {
	protected static ROVector2f[] poly = {v(-1,-28), v(3,-24), v(5,-16), v(6,-9), v(5,-3), v(8,-4), v(23,-4), v(23,0), v(9,8), v(5,4), v(6,13), v(-8,13), v(-8,4), v(-10,8), v(-24,1), v(-24,-4), v(-9,-4), v(-5,-2), v(-6,-8), v(-6,-16), v(-4,-25)};
	protected static Shape shape = new Polygon(poly);
	protected static double MAX = 1;
	protected static float A = 1;
	protected static int NUM_MISSILES = 10;
	protected Explosion explosion;
	protected double hull = MAX;
	protected int thrust;
	protected float accel, torque;
	protected boolean fire, explode, launch;
	protected World world;
	protected static final int ACTIVE_DEFAULT = 500;
	protected int activeTime = ACTIVE_DEFAULT;
	protected int textStatus = Integer.MAX_VALUE; // for blinking only
	protected long warningStart; // end of warning -> not invincible
	protected int missiles = NUM_MISSILES;
	protected long invincibleEnd; // end of invincibility -> warning(warntime)
	protected WeaponSys weapons;
	protected WeaponSys missileSys;
	public int deaths;

	public void reset() {
		BodyList excluded = getExcludedList();
		while (excluded.size() > 0)
			removeExcludedBody(excluded.get(0));
		setRotation(0);
		setPosition(0,0);
		adjustVelocity(MathUtil.sub(v(0,0),getVelocity()));
		adjustAngularVelocity(-getAngularVelocity());
		missiles = NUM_MISSILES;
		accel = torque = 0;
		fire = explode = false;
		warningStart = invincibleEnd = 0;
		hull = MAX;
		thrust = 0;
		weapons.setRandomWeaponType();
		weapons.incrRandomWeaponLevel();
	}

	public void notifyInput() {
		activeTime = ACTIVE_DEFAULT;
	}

	public int numMissiles() {
		return missiles;
	}

	public void addMissiles(int num) {
		missiles += num;
	}

	public void launchMissile() {
		if (missiles > 0) {
			if (missileSys.fire())
				missiles--;
		}
	}

	public void addStatsListener(Stats s) {
		weapons.addStatsListener(s);	
		missileSys.addStatsListener(s);
	}

	public static void setMax(double damage) {
		MAX = damage;
	}

	public static void setSpeed(float speed) {
		A = speed;
	}

	public Ship(World w) {
		super("Your ship", shape, 1000f);
		world = w;
		weapons = new WeaponSys(this, world, null);
		Missile.setWorld(w);
		missileSys = new WeaponSys(this, world, new Missile());
		setRotDamping(4000);
		reset();
	}

	public void gainInvincibility(int time, int warn) {
		invincibleEnd = Timer.gameTime() + time;
		warningStart = invincibleEnd - warn;
	}

	public void collided(CollisionEvent event) {
		if (!isInvincible())
			hull -= Exploder.getDamage(event, this);
		explode = hull < 0;
	}

	public boolean canExplode() {
		return explode && !isInvincible();
	}
	
	// canExplode but also tracking explosions
	public boolean dead() {
		return canExplode() && explosion != null && explosion.dead();
	}

	public int getTrust() {
		return thrust;
	}

	public float getTextureScaleFactor() {
		return 1.0f;
	}

	public Body getRemnant() {
		// assume 1 death if explode
		deaths++;
		return explosion = new LargeExplosion(1.5f);
	}

	public List<Body> getFragments() {
		List<Body> f = new ArrayList<Body>(11);
		for (int i=0; i < 11; i++) {
			HexAsteroid tmp = new HexAsteroid(range(4,9));
			tmp.setColor(Color.GRAY);
			f.add(tmp);
		}
		return f;
	}

	public Color getColor() {
		long time = Timer.gameTime();
		if (isInvincible()) {
			if (time < warningStart || textStatus-- % 10 > 5)
				return Color.GREEN;
		}
		if (getDamage() < .2)
			return Color.RED;
		else if (getDamage() < .6)
			return Color.YELLOW;
		return AbstractGame.COLOR;
	}

	public String getTexturePath() {
		return thrust > 0 ? "pixmaps/ship-t.png" : "pixmaps/ship.png";
	}

	public Vector2f getTextureCenter() {
		return v(32,32);
	}

	/**
	 * @return	Percent damage from max.
	 */
	public double getDamage() {
		return hull < 0 ? 0 : hull/MAX;
	}

	public boolean isInvincible() {
		return invincibleEnd > Timer.gameTime();
	}

	public void drawTo(Graphics2D g2d, ROVector2f o) {
		Polygon poly = (Polygon)getShape();
		ROVector2f[] verts = poly.getVertices(getPosition(), getRotation());
		int[] xcoords = new int[verts.length];
		int[] ycoords = new int[verts.length];
		for (int i=0; i < verts.length; i++) {
			xcoords[i] = (int)(verts[i].getX() - o.getX());
			ycoords[i] = (int)(verts[i].getY() - o.getY());
		}
		g2d.fillPolygon(xcoords, ycoords, verts.length);
	}

	public float getRadius() {
		return 45;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT: torque = -8e-5f; notifyInput(); break;
			case KeyEvent.VK_RIGHT: torque = 8e-5f; notifyInput(); break;
			case KeyEvent.VK_UP: accel = 30*A; notifyInput(); break;
			case KeyEvent.VK_DOWN: accel = -15*A; notifyInput(); break;
			case KeyEvent.VK_SPACE: fire = true; notifyInput(); break;
			case KeyEvent.VK_F: launch = true; notifyInput(); break;
		}
		if (e.getKeyChar() == '\'')
			fire = true;
	}

	public void keyReleased(KeyEvent e) {
		notifyInput();
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: torque = 0; notifyInput(); break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN: accel = 0; notifyInput(); break;
			case KeyEvent.VK_SPACE: fire = false; notifyInput(); break;
		}
		if (e.getKeyChar() == '\'')
			fire = false;
	}

	public void endFrame() {
		super.endFrame();
		float v = getVelocity().length();
		setDamping(v < 50 ? 0 : v < 100 ? .1f : .5f);
		thrust--;
		accel();
		torque();
		if (fire)
			weapons.fire();
		if (launch) {
			launchMissile();
			launch = false;
		}
		weapons.update();
		missileSys.update();
	}

	protected void accel() {
		if (accel > 0)
			thrust = 5;
		Vector2f dir = direction(getRotation());
		addForce(v(accel*getMass()*dir.getX(),accel*getMass()*dir.getY()));
	}

	protected void torque() {
		// setTorque() is unpredictable with varied dt
		adjustAngularVelocity(getMass()*torque);
	}

	public void keyTyped(KeyEvent e) {
		// don't care
	}
	
	public void setArmor(double num) {
		hull = num;
	}
}
