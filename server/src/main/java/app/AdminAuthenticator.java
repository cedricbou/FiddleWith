package app;


import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

import domain.User;

public class AdminAuthenticator implements Authenticator<BasicCredentials, User> {

	@Override
	public Optional<User> authenticate(BasicCredentials credentials)
			throws AuthenticationException {
		if ("admin".equals(credentials.getUsername())
				&& "fiddlewithit".equals(credentials.getPassword())) {
			return Optional.of(new User(credentials.getUsername(), "admin"));
		}
		return Optional.absent();
	}
}
