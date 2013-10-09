package domain.http;

import fiddle.api.WorkspaceId;

public class ScopedFiddleHttpRegistry {

	private final WorkspaceId workspaceId;
	private final FiddleHttpRegistry http;
	
	public ScopedFiddleHttpRegistry(final WorkspaceId workspaceId, final FiddleHttpRegistry http) {
		this.workspaceId = workspaceId;
		this.http = http;
	}
	
	public ScopedFiddleHttpClient scoped(final String client) {
		return new ScopedFiddleHttpClient(workspaceId, http.http(client));
	}

	public ConfiguredFiddleHttpClient configured(final String client) {
		return new ScopedFiddleHttpClient(workspaceId, http.http(client)).configured(http.uri(client), http.template(client));
	}

	public WorkspaceId workspaceId() {
		return workspaceId;
	}
	
}
