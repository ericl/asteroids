package asteroids.bodies;

import asteroids.ai.*;

import java.lang.reflect.*;

public class DynamicEntity implements InvocationHandler {
	private Entity e;
	private AI ai;

	public DynamicEntity(Entity e) {
		if (e == null)
			throw new IllegalArgumentException("no nulls permitted");
		this.e = e;
	}

	public void setAI(AI ai) {
		this.ai = ai;
		ai.setShip(e);
		e.setAI(ai);
	}

	public Entity newProxyInstance() {
		return (Entity)Proxy.newProxyInstance(
			Entity.class.getClassLoader(),
			new Class[] { Entity.class },
			this
		);
	}

	public void setEntity(Entity e) {
		this.e = e;
		if (ai != null) {
			ai.setShip(e);
			e.setAI(ai);
		}
	}

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		Object result = null;
		try {
			result = m.invoke(e, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
}
