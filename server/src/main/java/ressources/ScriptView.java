package ressources;

import com.yammer.dropwizard.views.View;

public class ScriptView extends View {

	private final String script;
	
	public ScriptView(final String script) {
		super("script.view.mustache");
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}
}
