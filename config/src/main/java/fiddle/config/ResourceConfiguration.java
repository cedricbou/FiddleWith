package fiddle.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class ResourceConfiguration {

	@JsonProperty
	@NotNull
	@Valid
	private ImmutableMap<String, DatabaseConfiguration> databases = ImmutableMap.of();
	
	public ImmutableMap<String, DatabaseConfiguration> getDatabases() {
		return databases;
	}
}
