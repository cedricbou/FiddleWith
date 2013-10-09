package fiddle.api;

public enum Language {
	RUBY("rb");

	public final String suffix;
	
	private Language(final String suffix) {
		this.suffix = suffix;
	}
	
}
