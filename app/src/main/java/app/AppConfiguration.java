package app;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import fiddle.config.Resources;

public class AppConfiguration extends Configuration {

	@JsonProperty
	@Valid
	private final Resources resources = Resources.EMPTY;
	
	public Resources getGlobalResources() {
		return resources;
	}
}
