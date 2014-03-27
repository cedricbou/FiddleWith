package fiddle.resources;

import org.apache.camel.CamelContext;

import fiddle.api.WorkspaceId;
import fiddle.config.ResourceConfiguration;
import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.resources.builder.DbiResourceBuilder;
import fiddle.resources.builder.HttpResourceBuilder;
import fiddle.resources.impl.CamelResources;
import fiddle.resources.impl.DbiResources;
import fiddle.resources.impl.HttpResources;

public class WorkspaceResources implements Resources {

	private final HttpResources https;
	private final DbiResources dbis;

	public WorkspaceResources(final WorkspaceId wId,
			final ResourceConfiguration config, final DbiResourceBuilder dbiBuilder, final HttpResourceBuilder httpBuilder) {
		
		this.https = new HttpResources(wId, config.getHttpClients(), httpBuilder);
		this.dbis = new DbiResources(wId, config, dbiBuilder);
	}

	@Override
	public DecoratedDbi dbi(String id) {
		return dbis.dbi(id);
	}

	public FiddleHttpClient http() {
		return https.http();
	};

	@Override
	public ConfiguredFiddleHttpClient http(String id) {
		return https.http(id);
	}
	
	@Override
	public CamelContext camel() {
		return CamelResources.camel;
	}
}
