package domain.sql;

import java.util.HashMap;
import java.util.Map;

import org.skife.jdbi.v2.DBI;


public class FiddleSqlRegistry {
	
	private final Map<String, DBI> dbis = new HashMap<String, DBI>();
	
	public FiddleSqlRegistry() {
	}

	public void declare(final String key, final DBI jdbi) {
		dbis.put(key, jdbi);
	}
	
	public FiddleSqlReadOnly get(final String key) {
		return new FiddleSqlReadOnly(dbis.get(key));
	}
}
