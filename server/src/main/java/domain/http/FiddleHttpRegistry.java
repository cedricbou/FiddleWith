package domain.http;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.TemplateId;
import domain.WorkspaceId;
import domain.template.FiddleTemplateRenderer;

public class FiddleHttpRegistry {

	private static final Logger LOG = LoggerFactory.getLogger(FiddleHttpRegistry.class);
	
	private final HttpRegistry http;
	private final FiddleTemplateRenderer templateRenderer;
	
	public static class ConfiguredFiddleHttpClient {
		
		public final FiddleHttpClient http;
		public final URI uri;
		public final TemplateId template;
		
		public ConfiguredFiddleHttpClient(final FiddleHttpClient http, URI uri, TemplateId template) {
			this.http = http;
			this.uri = uri;
			this.template = template;
		}
	}
	
	public FiddleHttpRegistry(final HttpRegistry http, final FiddleTemplateRenderer renderer) {
		this.http = http;
		this.templateRenderer = renderer;
	}
	
	protected FiddleHttpClient http(final String name) {
		LOG.debug("get fiddle http client for {}", name);
		return new FiddleHttpClient(templateRenderer, http.get(name).http);
	}
	
	protected URI uri(final String name) {
		return http.get(name).uri;
	}
	
	protected TemplateId template(final String name) {
		return http.get(name).template;
	}
	
	public ScopedFiddleHttpRegistry scoped(final WorkspaceId workspaceId) {
		return new ScopedFiddleHttpRegistry(workspaceId, this);
	}
}
