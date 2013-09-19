package app;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.HttpClientConfiguration;

import domain.TemplateId;

public class FiddleHttpConfiguration {

	public FiddleHttpConfiguration() {
		
	}
	
	@Valid
	@JsonProperty
	private TemplateId template = TemplateId.NONE;
	
	@Valid
	@NotNull
	@JsonProperty
	private String uri;
	
	@Valid
	@NotNull
	@JsonProperty
	private HttpClientConfiguration config = new HttpClientConfiguration();
	
	public TemplateId template() {
		return template;
	}
	
	public URI uri() {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpClientConfiguration config() {
		return config;
	}
}
