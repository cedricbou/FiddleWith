package fiddle.config.http;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientConfiguration;

public class ConfiguredHttpConfiguration {

	@JsonProperty
	@Valid
	@NotNull
	private HttpClientConfiguration client = new HttpClientConfiguration();
	
	@JsonProperty(required = true)
	@Valid
	@NotNull
	private String url;
	
	@JsonProperty
	@Valid
	@NotNull
	private String data = "";
	
	@JsonProperty
	@Valid
	@NotNull
	private ImmutableMap<String, String> headers = ImmutableMap.of();

	public HttpClientConfiguration client() {
		return client;
	}
	
	public String url() {
		return url;
	}
	
}
