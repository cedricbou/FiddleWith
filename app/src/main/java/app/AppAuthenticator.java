package app;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

import fiddle.password.PasswordManager;
import fiddle.password.PasswordManager.UserConfiguration;

public class AppAuthenticator implements
		Authenticator<BasicCredentials, PasswordManager.UserConfiguration> {

	private final PasswordManager passwords;
	
	public AppAuthenticator(final PasswordManager passwords) {
		this.passwords = passwords;
	}
	
	@Override
	public Optional<UserConfiguration> authenticate(BasicCredentials credentials)
			throws AuthenticationException {
		
		final Optional<UserConfiguration> uc = passwords.findUser(credentials.getUsername());
		
		if(uc.isPresent() && uc.get().validatePassword(credentials.getPassword())) {
			return uc;
		}
		else {
			return Optional.absent();
		}
	}
}
