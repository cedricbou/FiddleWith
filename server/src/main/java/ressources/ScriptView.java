package ressources;

import com.yammer.dropwizard.views.View;

import domain.Fiddle;
import domain.FiddleId;
import domain.repo.Language;

public class ScriptView extends View {

	private final Fiddle fiddle;
	private final FiddleId id;
	
	public ScriptView(final FiddleId id, final Fiddle fiddle) {
		super("script.view.mustache");
		this.fiddle = fiddle;
		this.id = id;
	}
	
	public String getScript() {
		return fiddle.getScript();
	}
	
	public String getId() {
		return id.toString();
	}
	
	public Language getLanguage() {
		return fiddle.getLanguage();
	}
}
