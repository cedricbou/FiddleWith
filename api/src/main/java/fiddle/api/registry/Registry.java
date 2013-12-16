package fiddle.api.registry;

public interface Registry<T> {

	public abstract void declare(String key, T entry);

	public abstract T get(String key);

	public abstract boolean contains(String key);

}