package fiddle.dbi;

import org.skife.jdbi.v2.DBI;

import fiddle.api.commons.Registry;

public class DbiRegistry extends Registry<DBI> {

	public DbiRegistry() {
		super();
	}
	
	public DecoratedDbi getDecoratedDbi(final String key) {
		return new DecoratedDbi(get(key));
	}
	
}
