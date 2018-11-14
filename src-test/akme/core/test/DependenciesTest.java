package akme.core.test;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import junit.framework.TestCase;
import akme.core.util.Dependencies;

/**
 * Test cases for Dependencies which extends Properties.
 */
public class DependenciesTest extends TestCase {

	private Dependencies that;
	private Properties other;

	@Override
	protected void setUp() throws Exception {
		that = new Dependencies();
		other = new Properties();
	}

	public void testGenericsAvoidCasts() {
		Properties x;
		x = that.get("Properties", null);
		assertEquals(x, null);
		x = that.get("Properties", other);
		assertEquals(x, other);
		x = that.get(Properties.class, other);
		assertEquals(x, other);
		x = that.get(Properties.class, that);
		assertEquals(x, that);
		x = that.get(Dependencies.class, that);
		assertEquals(x, that);
	}

	public void testMissingDependencyWithoutDefault() {
		try {
			that.get(Properties.class);
			fail("expectedExceptions");
		} catch (final IllegalStateException ex) {
			; // expectedExceptions
		}
	}

	public void testValidDependencies() {
		Properties x;
		final Properties y = new Properties(), z = new Dependencies();
		that.put(Properties.class, y);
		x = that.get(Properties.class);
		assertEquals(x, y);
		x = that.get(Properties.class, z);
		assertEquals(x, y);
	}

	public void testGetCallable() {
		final Dependencies z = new Dependencies();
		z.put(Long.class, new Callable<Long>() {
			@Override
			public Long call() {
				return Long.valueOf(1);
			}
		});
		assertEquals(Long.valueOf(1), z.get(Long.class));
		assertEquals(Long.valueOf(1), z.get(Long.class, Long.valueOf(2)));

		z.put(Long.class, new Callable<Long>() {
			@Override
			public Long call() {
				return null;
			}
		});
		assertEquals(Long.valueOf(2), z.get(Long.class, Long.valueOf(2)));
	}

	public void testPutCallableWrapped() {
		final Dependencies z = new Dependencies();
		final Callable<?> c = new Callable<Long>() {
			@Override
			public Long call() {
				return Long.valueOf(1);
			}
		};
		z.putCallableWrapped(Callable.class, c);
		assertSame(c, z.get(Callable.class));
	}

	public void testGetSupplier() {
		final Dependencies z = new Dependencies();
		final String key = "Supplier<Long>";
		z.put(key, (Supplier<Long>) () -> Long.valueOf(1));
		assertEquals(Long.valueOf(1), z.get(key));
		assertEquals(Long.valueOf(1), z.get(key, Long.valueOf(2)));

		z.put(key, (Supplier<Long>) () -> null);
		assertEquals(Long.valueOf(2), z.get(key, Long.valueOf(2)));
	}

	public void testPutSupplierWrapped() {
		final Dependencies z = new Dependencies();
		final Supplier<?> s = (Supplier<Long>) () -> Long.valueOf(1);
		z.putSupplierWrapped(Supplier.class, s);
		assertSame(s, z.get(Supplier.class));
	}

//	/*
//	 * Not workable since when asked for a subclass this may return a superclass, but left as an example of traversing
//	 * the superclass and interface tree. Better to put and get dependencies as a specific superclass or interface.
//	 */
//	@SuppressWarnings("unchecked")
//	protected <V> V getRelated(Class<V> key, V defaultValue, boolean useDefault) {
//		V val = null;
//		for (Class<?> cls = key;
//				val == null && cls != null;
//				cls = cls.getSuperclass()) {
//			val = (V) super.get(cls);
//			if (val == null) for (Class<?> cli : ((Class<?>) cls).getInterfaces()) {
//				val = (V) super.get(cli);
//				if (val != null) break;
//			}
//		}
//		if (val == null && defaults != null) {
//			val = (V) defaults.get(key);
//		}
//		if (val == null && !useDefault) {
//			throw new IllegalStateException("Missing dependency "+ key);
//		} else if (val instanceof Callable<?>) {
//			try {
//				val = ((Callable<V>) val).call();
//			} catch (Exception ex) {
//				throw new IllegalStateException(ex.getMessage() + " Cause: " + ex.getCause(), ex);
//			}
//		}
//		return (val == null) ? defaultValue : val;
//	}

}
