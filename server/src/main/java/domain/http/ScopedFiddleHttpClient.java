package domain.http;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.google.common.base.Optional;

import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;

public class ScopedFiddleHttpClient {

	private final WorkspaceId workspaceId;
	private final FiddleHttpClient http;

	public ScopedFiddleHttpClient(final WorkspaceId workspaceId,
			final FiddleHttpClient http) {
		this.workspaceId = workspaceId;
		this.http = http;
	}

	public ConfiguredFiddleHttpClient configured(final URI uri, final TemplateId template) {
		return new ConfiguredFiddleHttpClient(uri, template, this);
	}

	public FiddleHttpResponse get(URI uri, final Optional<Map<String, Object>> queryString)
			throws IOException {
		return http.get(uri, queryString);
	}

	public FiddleHttpResponse get(URI uri, final Optional<Map<String, Object>> queryString, Optional<TemplateId> templateId, Optional<Object> entity)
			throws IOException {
		return http.get(uri, queryString, workspaceId, templateId, entity);
	}

	public FiddleHttpResponse post(URI uri, final Optional<Map<String, Object>> queryString)
			throws IOException {
		return http.post(uri, queryString);
	}
	
	public FiddleHttpResponse post(URI uri, final Optional<Map<String, Object>> queryString, Optional<TemplateId> templateId, Optional<Object> entity)
			throws IOException {
		return http.post(uri, queryString, workspaceId, templateId, entity);
	}
}
