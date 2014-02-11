package fiddle.resources.builder;

import org.skife.jdbi.v2.DBI;

import com.yammer.dropwizard.db.DatabaseConfiguration;

public interface DbiResourceBuilder {
	public DBI buildDbiFromConfig(final String id,
			final DatabaseConfiguration config);

}
