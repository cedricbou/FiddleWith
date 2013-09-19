package domain.ruby.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import domain.TemplateId;
import domain.http.FiddleHttpClient;
import domain.http.FiddleHttpResponse;
import domain.http.ScopedFiddleHttpRegistry;

public class JRubyHttp {

	private final ScopedFiddleHttpRegistry http;
	
	private final FiddleHttpClient defaultHttp;
	
	
	public JRubyHttp(final ScopedFiddleHttpRegistry http, final FiddleHttpClient defaultHttp) {
		this.http = http;
		this.defaultHttp = defaultHttp;
	}
	
	public JRubyConfiguredHttp http(final String key) {
		return new JRubyConfiguredHttp(http.configured(key));
	}
	
	public FiddleHttpResponse post(final String url, final String template, final Object entity) {
		try {
			return defaultHttp.post(new URI(url), http.workspaceId(), new TemplateId(template), entity);
		}
		catch(URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse post(final String url, final String template) {
		try {
			return defaultHttp.post(new URI(url), http.workspaceId(), new TemplateId(template));
		}
		catch(URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse get(final String url, final String template, final Object entity) {
		try {
			return defaultHttp.get(new URI(url), http.workspaceId(), new TemplateId(template), entity);
		}
		catch(URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public FiddleHttpResponse get(final String url, final String template) {
		try {
			return defaultHttp.post(new URI(url), http.workspaceId(), new TemplateId(template));
		}
		catch(URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
