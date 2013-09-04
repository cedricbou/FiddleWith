package ressources;

import com.yammer.dropwizard.views.View;

import domain.Fiddle;
import domain.repo.Language;

public class ScriptView extends View {

	private final Fiddle fiddle;
	private final String id;
	
	public ScriptView(final String id, final Fiddle fiddle) {
		super("script.view.mustache");
		this.fiddle = fiddle;
		this.id = id;
	}
	
	public String getScript() {
		return fiddle.getScript();
	}
	
	public String getId() {
		return id;
	}
	
	public Language getLanguage() {
		return fiddle.getLanguage();
	}
}
