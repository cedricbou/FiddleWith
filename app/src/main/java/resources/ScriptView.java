package resources;

import com.yammer.dropwizard.views.View;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.WorkspaceId;

public class ScriptView extends View {

	private final Fiddle fiddle;
	private final FiddleId id;
	private final WorkspaceId workspace;
	
	public ScriptView(final WorkspaceId workspace, final FiddleId id, final Fiddle fiddle) {
		super("script.view.mustache");
		this.fiddle = fiddle;
		this.id = id;
		this.workspace = workspace;
	}
	
	public String getScript() {
		return fiddle.content;
	}
	
	public String getId() {
		return id.toString();
	}
	
	public String getWorkspace() {
		return workspace.toString();
	}
	
	public Language getLanguage() {
		return fiddle.language;
	}
}
