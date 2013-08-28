package domain;

import domain.ruby.RubyEnv;
import domain.sql.FiddleSqlRegistry;

public class FiddleEnvironment {
	
	public final FiddleSqlRegistry sql;
	public final RubyEnv ruby;
	public final FiddleRepository repository;
	
	public FiddleEnvironment(final FiddleSqlRegistry sql) {
		this.sql = sql;
		this.ruby = new RubyEnv(sql);
		this.repository = new FiddleRepository(this);
	}

}
