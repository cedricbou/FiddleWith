package fiddle.api.registry;

class SingleRegistry<T extends Registry<?>> implements SelectRegistry<T> {

	private final T main;

	protected SingleRegistry(final T main) {
		this.main = main;
	}

	@Override
	public T forKey(final String key) {
		return main;
	}
}
