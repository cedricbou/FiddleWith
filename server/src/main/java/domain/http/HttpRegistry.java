package domain.http;

import java.net.URI;

import org.apache.http.impl.client.DefaultHttpClient;

import domain.FiddleRegistry;
import domain.http.HttpRegistry.ConfiguredHttpClient;
import domain.template.FiddleTemplateRenderer;
import fiddle.api.TemplateId;

public class HttpRegistry extends FiddleRegistry<ConfiguredHttpClient> {

	public static class ConfiguredHttpClient {
		
		public final DefaultHttpClient http;
		public final URI uri;
		public final TemplateId template;
		
		public ConfiguredHttpClient(final DefaultHttpClient http, URI uri, TemplateId template) {
			this.http = http;
			this.uri = uri;
			this.template = template;
		}
	}
	
	public HttpRegistry() {
		super();
	}
	
	public FiddleHttpRegistry withTemplateRenderer(final FiddleTemplateRenderer renderer) {
		return new FiddleHttpRegistry(this, renderer);
	}
}
