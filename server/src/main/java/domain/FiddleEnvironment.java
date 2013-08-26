package domain;

import org.skife.jdbi.v2.DBI;

import com.google.common.base.Optional;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

/**
 * Provides a context to initialize dependencies for the fiddle domain.
 * 
 * @author Cedric
 * 
 */
public class FiddleEnvironment {

	private final DBIFactory factory;
	private final Environment env;

	public FiddleEnvironment(final Environment env, final DBIFactory factory) {
		this.env = env;
		this.factory = factory;
	}

	public Optional<FiddleConfiguration> configuration() {
		return Optional.absent();
	}

	public FiddleDb databases() {
		final FiddleDb db = new FiddleDb();

		final Optional<FiddleConfiguration> config = configuration();

		if (config.isPresent()) {
			for (final String database : config.get().databases()) {
				final Optional<DatabaseConfiguration> dbConfig = config.get()
						.database(database);

				if (dbConfig.isPresent()) {
					try {
						final DBI jdbi = factory.build(env, dbConfig.get(),
								"mysql" /*
										 * TODO: hard coded piece to remove
										 */);
						
						db.declare(jdbi);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(
								"failed to initiate jdbi for " + database + ".",
								e);
					}
				}
			}
		}

		return db;
	}

}