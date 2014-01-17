package fiddle.resources.http;

import java.util.HashMap;
import java.util.Map;

import org.skife.jdbi.v2.DBI;

import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

import fiddle.config.ResourceConfiguration;
import fiddle.dbi.DecoratedDbi;

public class DbiResources {
	
	private final Map<String, DBI> dbis = new HashMap<String, DBI>();
	
	public DbiResources(final ResourceConfiguration config, final Environment env) {
		final DBIFactory dbiFactory = new DBIFactory();
		
		for(final Map.Entry<String, DatabaseConfiguration> entry : config.getDatabases().entrySet()) {
			dbis.put(entry.getKey(), buildDbi(entry.getKey(), env, dbiFactory, entry.getValue()));
		}
	}
	
	private final DBI buildDbi(final String id, final Environment env, final DBIFactory dbiFactory, final DatabaseConfiguration config) {
		try {
			return dbiFactory.build(env, config, id);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(
					"failed to build dbi with provided factory", cnfe);
		}
	}
	
	public DecoratedDbi dbi(final String id) {
		final DBI dbi = dbis.get(id);
		
		if(null == dbi) {
			return null;
		}
		
		return new DecoratedDbi(dbi);
	}
}
