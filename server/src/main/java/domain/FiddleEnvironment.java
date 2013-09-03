package domain;

import java.io.File;

import domain.repo.FiddleRepository;
import domain.ruby.RubyEnv;
import domain.sql.FiddleSqlRegistry;

public class FiddleEnvironment {
	
	public final FiddleSqlRegistry sql;
	public final RubyEnv ruby;
	public final FiddleRepository repository;
	
	public FiddleEnvironment(final FiddleSqlRegistry sql, final File fiddleRepository) {
		this.sql = sql;
		this.ruby = new RubyEnv(sql);
		this.repository = new FiddleRepository(fiddleRepository, ruby);
	}

}
