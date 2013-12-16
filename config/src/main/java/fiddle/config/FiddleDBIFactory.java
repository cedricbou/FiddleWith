package fiddle.config;

import org.skife.jdbi.v2.DBI;

import com.yammer.dropwizard.db.DatabaseConfiguration;

public interface FiddleDBIFactory {

	public DBI build(final DatabaseConfiguration config);
}
