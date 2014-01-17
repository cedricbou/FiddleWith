package fiddle.resources.http;

import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.yammer.dropwizard.client.HttpClientBuilder;

import fiddle.api.TemplateId;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.config.http.FiddleHttpConfiguration;
import fiddle.httpclient.BodyBuilder;
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

		final ConfiguredFiddleHttpClient withBody;

		if (config.configured().get(id).body() != null) {
			final Optional<Mustache> body = templates.open(new TemplateId(
					config.configured().get(id).body()));

			if (body.isPresent()) {
				withBody = configured
						.withBody(new BodyBuilder.MustacheBodyBuilder(body
								.get()));
			} else {
				withBody = configured.withBody(config.configured().get(id)
						.body());
			}
		} else {
			withBody = configured;
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
