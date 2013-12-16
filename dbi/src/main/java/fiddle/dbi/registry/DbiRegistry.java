package fiddle.dbi.registry;

import org.skife.jdbi.v2.DBI;

import fiddle.api.registry.Registry;
import fiddle.dbi.DecoratedDbi;

public interface DbiRegistry extends Registry<DBI> {

	public abstract DecoratedDbi getDecoratedDbi(String key);

}