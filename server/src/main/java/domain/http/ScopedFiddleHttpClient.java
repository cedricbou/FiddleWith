package domain.http;

import java.io.IOException;
import java.net.URI;

import domain.TemplateId;
import domain.WorkspaceId;

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
	
	public FiddleHttpResponse get(URI uri, TemplateId templateId)
			throws IOException {
		return http.get(uri, workspaceId, templateId);
	}

	public FiddleHttpResponse get(URI uri, TemplateId templateId, Object entity)
			throws IOException {
		return http.get(uri, workspaceId, templateId, entity);
	}

	public FiddleHttpResponse post(URI uri, TemplateId templateId)
			throws IOException {
		return http.post(uri, workspaceId, templateId);
	}

	public FiddleHttpResponse post(URI uri, TemplateId templateId, Object entity)
			throws IOException {
		return http.post(uri, workspaceId, templateId, entity);
	}
}
