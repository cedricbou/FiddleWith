package domain.sql;

import org.skife.jdbi.v2.DBI;

import domain.FiddleRegistry;


public class FiddleSqlRegistry extends FiddleRegistry<DBI>{
		
	public FiddleSqlRegistry() {
		super();
	}
	
	public FiddleSqlReadOnly getRO(final String key) {
		return new FiddleSqlReadOnly(this.get(key));
	}
}
