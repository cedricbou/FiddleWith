package fiddle.api.registry;

public interface SelectRegistry<T extends Registry<?>> {

	public T forKey(final String key);
}
