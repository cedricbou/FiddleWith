package app;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.yammer.dropwizard.config.Configuration;

import fiddle.config.ResourceConfiguration;
import fiddle.password.PasswordManager;

public class AppConfiguration extends Configuration {

	@JsonProperty
	@Valid
	private String repository;

	@JsonProperty
	@Valid
	private ImmutableList<UserConfiguration> users = ImmutableList.of();

	@JsonProperty
	@Valid
	private ResourceConfiguration resources;
	
	
	public ResourceConfiguration getGlobalResourceConfiguration() {
		return resources;
	}
	
	public String getRepository() {
		return repository;
	}
	
	public PasswordManager getUsers() {
		final PasswordManager manager = new PasswordManager();
		
		for(final UserConfiguration uc : users) {
			manager.declareUser(uc.getPasswordUserConfiguration());
		}
		
		return manager;
	}
}
