package domain.ruby;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import domain.WorkspaceId;
import domain.http.FiddleHttpClient;
import domain.http.FiddleHttpRegistry;
import domain.ruby.http.JRubyHttp;
import domain.ruby.sql.JRubySqlRegistry;
import domain.sql.FiddleSqlRegistry;

public class RubyEnv {

	public final JRubySqlRegistry sql;
	
	private final FiddleHttpClient defaultHttp;
	private final FiddleHttpRegistry http;
	
	public final ScriptingContainer container;
	
	public RubyEnv(final FiddleSqlRegistry sql, final FiddleHttpClient defaultHttp, final FiddleHttpRegistry http) {
		this.container = new ScriptingContainer(
				LocalVariableBehavior.TRANSIENT);
		
		this.sql = new JRubySqlRegistry(sql, container.getProvider().getRuntime());
		this.http = http;
		this.defaultHttp = defaultHttp;
	}
	
	public JRubyHttp http(final WorkspaceId workspaceId) {
		return new JRubyHttp(http.scoped(workspaceId), defaultHttp);
	}
}
