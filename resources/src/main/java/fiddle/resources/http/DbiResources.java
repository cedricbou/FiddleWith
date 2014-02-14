package fiddle.resources.http;

import java.util.HashMap;
import java.util.Map;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.db.DatabaseConfiguration;

import fiddle.api.WorkspaceId;
import fiddle.config.ResourceConfiguration;
import fiddle.dbi.DecoratedDbi;
import fiddle.resources.builder.DbiResourceBuilder;

public class DbiResources {

	private final Logger LOG = LoggerFactory.getLogger(DbiResources.class);

	private final Map<String, DBI> dbis = new HashMap<String, DBI>();
	
	public DbiResources(final WorkspaceId wId, final ResourceConfiguration config, final DbiResourceBuilder builder) {
		LOG.info("checking for dbi resources in " + wId);
		
		for(final Map.Entry<String, DatabaseConfiguration> entry : config.getDatabases().entrySet()) {
			LOG.info("building dbi pool for " + entry.getKey() + " in " + wId + " (" + entry.getValue().getDriverClass() + ":" + entry.getValue().getUrl() + ")");
			dbis.put(entry.getKey(), builder.buildDbiFromConfig(entry.getKey(), entry.getValue()));
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
