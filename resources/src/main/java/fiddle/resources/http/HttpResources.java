package fiddle.resources.http;

import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.yammer.dropwizard.client.HttpClientBuilder;

import fiddle.api.TemplateId;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.config.http.FiddleHttpConfiguration;
import fiddle.httpclient.TemplateStringBuilder;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.repository.Repository;

public class HttpResources {

	private final Map<String, ConfiguredFiddleHttpClient> https = new HashMap<String, ConfiguredFiddleHttpClient>();
	private final FiddleHttpConfiguration config;
	private final Repository<Mustache, String, TemplateId> templates;

	public HttpResources(
			final Repository<Mustache, String, TemplateId> templates,
			final FiddleHttpConfiguration config) {
		this.templates = templates;
		this.config = config;

		for (final Map.Entry<String, ConfiguredHttpConfiguration> configured : config
				.configured().entrySet()) {
			https.put(configured.getKey(),
					buildFromConfig(configured.getValue()));
		}
	}

	public ConfiguredFiddleHttpClient http(final String id) {
		final ConfiguredFiddleHttpClient configured = https.get(id);

		if (null == configured) {
			return null;
		}

		final ConfiguredFiddleHttpClient withHeaders;
		
		if(config.configured().get(id).headers() != null) {
			withHeaders = configured.withHeaders(config.configured().get(id).headers());
		}
		else {
			withHeaders = configured;
		}
		
		final ConfiguredFiddleHttpClient withBody;

		if (config.configured().get(id).body() != null) {
			final Optional<Mustache> body = templates.open(new TemplateId(
					config.configured().get(id).body()));

			if (body.isPresent()) {
				withBody = withHeaders
						.withBody(new TemplateStringBuilder.MustacheTemplateStringBuilder(body
								.get()));
			} else {
				withBody = withHeaders.withBody(config.configured().get(id)
						.body());
			}
		} else {
			withBody = withHeaders;
		}
		
		return withBody;
	}

	public FiddleHttpClient http() {
		return new FiddleHttpClient(new HttpClientBuilder().using(
				config.getDefault()).build());

	}

	private ConfiguredFiddleHttpClient buildFromConfig(
			final ConfiguredHttpConfiguration config) {
		// Constuct with URL
		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(
				new HttpClientBuilder().using(config.client()).build())
				.withUrl(config.url());

		return client;
	}

}
