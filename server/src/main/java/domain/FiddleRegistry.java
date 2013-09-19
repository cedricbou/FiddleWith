package domain;

import java.util.HashMap;
import java.util.Map;


public class FiddleRegistry<T> {
	
	private final Map<String, T> entries = new HashMap<String, T>();
	
	public FiddleRegistry() {
	}

	public void declare(final String key, final T entry) {
		entries.put(key, entry);
	}
	
	public T get(final String key) {
		return entries.get(key);
	}
}
