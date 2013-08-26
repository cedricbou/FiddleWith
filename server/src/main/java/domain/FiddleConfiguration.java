package domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class FiddleConfiguration {

	@Valid
	@NotNull
	@JsonProperty
	private ImmutableMap<String, DatabaseConfiguration> databases;

	public String[] databases() {
		return (String[])databases.keySet().toArray();
	}
	
	public Optional<DatabaseConfiguration> database(final String database) {
		if(databases.containsKey(database)) {
			return Optional.of(databases.get(database));
		}
		return Optional.absent();
	}

}
