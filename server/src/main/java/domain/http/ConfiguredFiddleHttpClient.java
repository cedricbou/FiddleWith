package domain.http;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.google.common.base.Optional;

import fiddle.api.TemplateId;

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

	public FiddleHttpResponse get() throws IOException {
		return http.get(uri, Optional.<Map<String, Object>> absent());
	}

	public FiddleHttpResponse get(Map<String, Object> queryString)
			throws IOException {
		return http.get(uri, Optional.of(queryString), Optional.of(template),
				Optional.absent());
	}

	public FiddleHttpResponse get(Optional<Map<String, Object>> queryString,
			Optional<Object> entity) throws IOException {
		return http.get(uri, queryString, Optional.of(template), entity);
	}

	public FiddleHttpResponse post() throws IOException {
		return http.post(uri, Optional.<Map<String, Object>> absent(),
				Optional.of(template), Optional.absent());
	}

	public FiddleHttpResponse post(Object entity) throws IOException {
		return http.post(uri, Optional.<Map<String, Object>> absent(),
				Optional.of(template), Optional.of(entity));
	}

	public FiddleHttpResponse post(Map<String, Object> queryString)
			throws IOException {
		return http.post(uri, Optional.of(queryString), Optional.of(template),
				Optional.absent());
	}

	public FiddleHttpResponse post(Map<String, Object> queryString,
			Object entity) throws IOException {
		return http.post(uri, Optional.of(queryString), Optional.of(template),
				Optional.of(entity));
	}

	public FiddleHttpResponse post(Optional<Map<String, Object>> queryString,
			Optional<Object> entity) throws IOException {
		return http.post(uri, queryString, Optional.of(template), entity);
	}

}
