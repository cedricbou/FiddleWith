package fiddle.resources.http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.client.HttpClientBuilder;

import fiddle.api.WorkspaceId;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.config.http.FiddleHttpConfiguration;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.resources.builder.HttpResourceBuilder;

public class HttpResources {

	private final Logger LOG = LoggerFactory.getLogger(HttpResources.class);
	
	private final Map<String, ConfiguredFiddleHttpClient> https = new HashMap<String, ConfiguredFiddleHttpClient>();
	private final FiddleHttpClient http;

	public HttpResources(
			final WorkspaceId wId, final FiddleHttpConfiguration config, final HttpResourceBuilder httpBuilder) {
		
		LOG.info("checking for http resources in " + wId);
		
		this.http = new FiddleHttpClient(new HttpClientBuilder().using(
				config.getDefault()).build());
		
		for (final Map.Entry<String, ConfiguredHttpConfiguration> configured : config
				.configured().entrySet()) {
			LOG.info("building http client for " + configured.getKey() + " in " + wId + " (" + configured.getValue().url() + ")");

			https.put(configured.getKey(),
					httpBuilder.buildFromConfig(wId, configured.getKey(), configured.getValue()));
		}
	}

	public ConfiguredFiddleHttpClient http(final String id) {
		final ConfiguredFiddleHttpClient configured = https.get(id);

		if (null == configured) {
			return null;
		}

		return configured;
	}

	public FiddleHttpClient http() {
		return http;
	}
}
