package fiddle.config.http;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientConfiguration;

public class FiddleHttpConfiguration {

	@JsonProperty("default")
	@Valid
	@NotNull
	private HttpClientConfiguration _default = new HttpClientConfiguration();
	
	@JsonProperty
	@Valid
	@NotNull
	private ImmutableMap<String, ConfiguredHttpConfiguration> configured = ImmutableMap.of();

	public HttpClientConfiguration getDefault() {
		return _default;
	}
	
	public ImmutableMap<String, ConfiguredHttpConfiguration> configured() {
		return configured;
	}
}
