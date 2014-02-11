package fiddle.resources.builder;

import fiddle.api.WorkspaceId;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.httpclient.ConfiguredFiddleHttpClient;

public interface HttpResourceBuilder {
	public ConfiguredFiddleHttpClient buildFromConfig(final WorkspaceId workspaceId, final String id, final ConfiguredHttpConfiguration config);

}
