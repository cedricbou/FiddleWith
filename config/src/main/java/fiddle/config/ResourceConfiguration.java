package fiddle.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import fiddle.config.http.FiddleHttpConfiguration;

public class ResourceConfiguration {

	@JsonProperty
	@NotNull
	@Valid
	private ImmutableMap<String, DatabaseConfiguration> databases = ImmutableMap.of();
	
	@JsonProperty
	@NotNull
	@Valid
	private FiddleHttpConfiguration http = new FiddleHttpConfiguration();
	
	public ImmutableMap<String, DatabaseConfiguration> getDatabases() {
		return databases;
	}
	
	public FiddleHttpConfiguration getHttpClients() {
		return http;
	}
	
	public ResourceConfiguration() {
		
	}
}
