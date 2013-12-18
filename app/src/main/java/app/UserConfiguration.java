package app;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import fiddle.password.PasswordManager;

public class UserConfiguration {

	@JsonProperty
	@Valid
	@NotEmpty
	private String user;
	
	@JsonProperty
	@Valid
	@NotEmpty
	private String hash;
	
	@JsonProperty
	@Valid
	private ImmutableSet<String> tags = ImmutableSet.of();
	
	public PasswordManager.UserConfiguration getPasswordUserConfiguration() {
		return new PasswordManager.UserConfiguration(user, hash, tags);
	}
	
}
