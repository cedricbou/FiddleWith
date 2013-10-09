package ressources;

import com.yammer.dropwizard.views.View;

import domain.ExecutableFiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;

public class ScriptView extends View {

	private final ExecutableFiddle fiddle;
	private final FiddleId id;
	
	public ScriptView(final FiddleId id, final ExecutableFiddle fiddle) {
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
