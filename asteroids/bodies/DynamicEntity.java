package asteroids.bodies;

import java.lang.reflect.*;

public class DynamicEntity implements InvocationHandler {
	private Entity e;

	public DynamicEntity(Entity e) {
		this.e = e;
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
