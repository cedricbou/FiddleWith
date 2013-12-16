package fiddle.api.registry;

import java.util.HashMap;
import java.util.Map;


public class BaseRegistry<T> implements Registry<T> {
	
	public final static <C extends Registry<?>> SelectRegistry<C> composed(C main, C fallback) {
		return new ComposedRegistry<C>(main, fallback);
	}

	public final static <C extends Registry<?>> SelectRegistry<C> single(C main) {
		return new SingleRegistry<C>(main);
	}

	
	private final Map<String, T> entries = new HashMap<String, T>();
	
	public BaseRegistry() {
	}

	/* (non-Javadoc)
	 * @see fiddle.api.registry.Registry#declare(java.lang.String, T)
	 */
	@Override
	public void declare(final String key, final T entry) {
		entries.put(key, entry);
	}
	
	/* (non-Javadoc)
	 * @see fiddle.api.registry.Registry#get(java.lang.String)
	 */
	@Override
	public T get(final String key) {
		return entries.get(key);
	}
	
	/* (non-Javadoc)
	 * @see fiddle.api.registry.Registry#contains(java.lang.String)
	 */
	@Override
	public boolean contains(final String key) {
		return entries.containsKey(key);
	}
}
