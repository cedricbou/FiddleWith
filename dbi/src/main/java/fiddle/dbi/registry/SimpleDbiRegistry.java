package fiddle.dbi.registry;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiddle.api.registry.BaseRegistry;
import fiddle.dbi.DecoratedDbi;

public class SimpleDbiRegistry extends BaseRegistry<DBI> implements DbiRegistry {

	private final Logger LOG = LoggerFactory.getLogger(SimpleDbiRegistry.class);
	
	public SimpleDbiRegistry() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see fiddle.dbi.registry.DbiRegistry#getDecoratedDbi(java.lang.String)
	 */
	@Override
	public DecoratedDbi getDecoratedDbi(final String key) {
		LOG.debug("get decorated dbi {}", key);
		return new DecoratedDbi(get(key));
		
	}
	
}
