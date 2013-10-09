package fiddle.api;

public class Fiddle {

	public final String content;
	public final Language language;
	
	public Fiddle(final String content, final Language language) {
		this.content = content;
		this.language = language;
	}
}
