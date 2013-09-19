package domain.http;

import java.io.IOException;
import java.net.URI;

import domain.TemplateId;

public class ConfiguredFiddleHttpClient {

	private final URI uri;
	private final TemplateId template;
	private final ScopedFiddleHttpClient http;

	public ConfiguredFiddleHttpClient(final URI uri, final TemplateId template,
			final ScopedFiddleHttpClient http) {
		this.uri = uri;
		this.template = template;
		this.http = http;
	}

	public FiddleHttpResponse get()
			throws IOException {
		return http.get(uri, template);
	}

	public FiddleHttpResponse get(Object entity)
			throws IOException {
		return http.get(uri, template, entity);
	}

	public FiddleHttpResponse post()
			throws IOException {
		return http.post(uri, template);
	}

	public FiddleHttpResponse post(Object entity)
			throws IOException {
		return http.post(uri, template, entity);
	}
}
