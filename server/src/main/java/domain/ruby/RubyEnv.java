package domain.ruby;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import domain.ruby.sql.JRubySqlRegistry;
import domain.sql.FiddleSqlRegistry;

public class RubyEnv {

	public final JRubySqlRegistry sql;
	public final ScriptingContainer container;
	
	public RubyEnv(final FiddleSqlRegistry fiddleSql) {
		this.container = new ScriptingContainer(
				LocalVariableBehavior.TRANSIENT);
		
		this.sql = new JRubySqlRegistry(fiddleSql, container.getProvider().getRuntime());
	}
}
