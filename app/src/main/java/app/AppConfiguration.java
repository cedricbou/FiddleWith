package app;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import fiddle.config.ResourceConfiguration;

public class AppConfiguration extends Configuration {

	@JsonProperty
	@Valid
	private String repository;
	
	@JsonProperty
	@Valid
	private ResourceConfiguration resources;
	
	public ResourceConfiguration getGlobalResourceConfiguration() {
		return resources;
	}
	
	public String getRepository() {
		return repository;
	}
}
