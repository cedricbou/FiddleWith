package fiddle.dbi.registry;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiddle.api.registry.BaseRegistry;
import fiddle.api.registry.SelectRegistry;
import fiddle.dbi.DecoratedDbi;

public class SelectDbiRegistry extends BaseRegistry<DBI> implements DbiRegistry {

	private final SelectRegistry<DbiRegistry> select;
	
	private final Logger LOG = LoggerFactory.getLogger(SelectDbiRegistry.class);

	public SelectDbiRegistry(final SelectRegistry<DbiRegistry> select) {
		this.select = select;
	}

	@Override
	public boolean contains(String key) {
		return select.forKey(key).contains(key);
	}

	@Override
	public void declare(String key, DBI entry) {
		throw new RuntimeException("declare not supported for Select registry");
	}

	@Override
	public DBI get(String key) {
		LOG.debug("get raw dbi {}", key);

		return select.forKey(key).get(key);
	}

	@Override
	public DecoratedDbi getDecoratedDbi(String key) {
		LOG.debug("get decorated dbi {}", key);

		return select.forKey(key).getDecoratedDbi(key);
	}
}
