package akme.core.util;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Dependencies to be injected via constructor, allowing ease of <code>Object get(Object key, Object default)</code>.
 * This provides final, i.e. set-once / write-once, protection on Dependencies.
 * New dependencies can be set, but existing dependencies cannot be reset or removed.
 * 
 * Use <code>V get(Class&lt;V&gt; key, V default)</code> to avoid having to cast.
 * TODO: write-once protection for entrySet(), keySet(), values().
 */
public class FinalDependencies extends Dependencies {

	private static final long serialVersionUID = -389261885864080820L;
	private static final String ALREADY_SET_FOR = "Dependency already set for ";

	public FinalDependencies() {
		super();
	}

	public FinalDependencies(Properties props) {
		super(props);
	}
	
	@Override
	public synchronized Object put(Object key, Object value) {
		if (!super.containsKey(key)) {
			return super.put(key, value);
		} else {
			throw new IllegalArgumentException(ALREADY_SET_FOR+ key);
		}
	}
	
	@Override
	public synchronized void putAll(Map<? extends Object, ? extends Object> map) {
		for (Map.Entry<? extends Object, ? extends Object> item : map.entrySet()) {
			if (!super.containsKey(item.getKey())) {
				throw new IllegalArgumentException(ALREADY_SET_FOR+ item.getKey());
			}
		}
		super.putAll(map);
	}
	
	/**
	 * Put the given Callable into the dependencies wrapped in another Callable
	 * such that a get(key) will return the original Callable itself.
	 * This simplifies setting and getting a Callable itself rather than getting what it returns.
	 */
	@Override
	public synchronized <V> Object putCallableWrapped(Object key, Callable<V> value) {
		if (!super.containsKey(key)) {
			return super.putCallableWrapped(key, new WrappedCallable<V>(value));
		} else {
			throw new IllegalArgumentException(ALREADY_SET_FOR+ key);
		}
	}
	
	@Override
	public synchronized Object remove(Object key) {
		throw new UnsupportedOperationException("remove(Object) not supported");
	}
	
	@Override
	public synchronized void clear() {
		throw new UnsupportedOperationException("clear() not supported");
	}

}
