package fiddle.api.registry;

class ComposedRegistry<T extends Registry<?>> implements SelectRegistry<T> {

	private final T main;
	private final T fallback;
	
	protected ComposedRegistry(final T main, final T fallback) {
		this.main = main;
		this.fallback = fallback;
	}
	
	@Override
	public T forKey(final String key) {
		if(main.contains(key)) {
			return main;
		}
		else {
			return fallback;
		}
	}
}
