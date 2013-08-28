package domain.ruby.sql;

import org.jruby.Ruby;

import domain.sql.FiddleSqlRegistry;

public class JRubySqlRegistry {

	private final FiddleSqlRegistry registry;
	
	private final Ruby ruby;
	
	public JRubySqlRegistry(final FiddleSqlRegistry registry, final Ruby ruby) {
		this.registry = registry;
		this.ruby = ruby;
	}
	
	public JRubySqlReadOnlyAdapter get(final String name) {
		return new JRubySqlReadOnlyAdapter(registry.get(name), ruby);
	}
}
