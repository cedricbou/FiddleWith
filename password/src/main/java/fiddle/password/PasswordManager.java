package fiddle.password;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class PasswordManager {

	private final static Logger LOG = LoggerFactory.getLogger(PasswordManager.class);
	
	public static class UserConfiguration {
		public final String user;
		private final String hash;
		private final ImmutableSet<String> tags;
		
		public UserConfiguration(final String user, final String hash, final ImmutableSet<String> tags) {
			this.user = user;
			this.hash = hash;
			this.tags = tags;
		}
		
		public boolean matchTag(final String tag) {
			return tags.contains(tag);
		}
		
		public boolean validatePassword(final String password) {
			try {
				return PasswordHash.validatePassword(password, hash);
			} catch(InvalidKeySpecException spec) {
				LOG.error("Failed to validate password because of invalid key spec", spec);
				return false;
			} catch(NoSuchAlgorithmException algo) {
				LOG.error("Failed to validate password, no such algorithm found", algo);
				return false;
			}
		}
		
		public void assertAdmin() throws IllegalAccessException {
			assertTag("admin");
		}
		
		public void assertTag(final String tag) throws IllegalAccessException{
			if(matchTag(tag)) {
				return;
			}
			throw new IllegalAccessException("Requires [" + tag + "] to use this feature.");
		}
		
		public void assertOneOf(final String... tags) throws IllegalAccessException {
			for(final String tag : tags) {
				if(matchTag(tag)) {
					return;
				}
			}
			throw new IllegalAccessException("Requires one of [" + Joiner.on(", ").join(tags) + "] to use this feature.");
		}
		
		public void assertAllOf(final String... tags) throws IllegalAccessException {
			for(final String tag : tags) {
				assertTag(tag);
			}
		}
		
	}
	
	private final Map<String, UserConfiguration> users = new HashMap<String, UserConfiguration>();
	
	public void declareUser(final UserConfiguration uc) {
		users.put(uc.user, uc);
	}
	
	public Optional<UserConfiguration> findUser(final String user) {
		return Optional.fromNullable(users.get(user));
	}
}
