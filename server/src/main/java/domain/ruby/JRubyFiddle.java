package domain.ruby;

import javax.ws.rs.core.Response;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.Fiddle;
import domain.FiddleEnvironment;
import domain.FiddleResponse;
import domain.WorkspaceId;
import domain.json.serializer.RubyJacksonModule;
import domain.repo.Language;

public class JRubyFiddle implements Fiddle {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	{
		mapper.registerModule(RubyJacksonModule.MODULE);
	}
	
	private final FiddleEnvironment env;
	private final RubyScript script;
	private final WorkspaceId workspaceId;
	
	public JRubyFiddle(final WorkspaceId workspaceId, final FiddleEnvironment env, final RubyScript script) {
		this.env = env;
		this.script = script;
		this.workspaceId = workspaceId;
	}

	@Override
	public Response execute(final JsonNode data) {

		final ScriptingContainer ruby = new ScriptingContainer(
				LocalVariableBehavior.TRANSIENT);

		// Assign the Java objects that you want to share
		ruby.put("response", new FiddleResponse(mapper));
		ruby.put("_json", data);
		ruby.put("_sql", env.ruby.sql);
		ruby.put("_http_registry", env.http.scoped(workspaceId));
		ruby.put("_http", env.defaultHttp);

		// Execute a script (can be of any length, and taken from a file)

		Object result = ruby.runScriptlet(script.script);

		// Use the result as if it were a Java object
		System.out.println(result);

		if (result instanceof Response) {
			return (Response) result;
		} else {
			return Response.status(500)
					.tag("Script did not return response object").build();
		}
	}
	
	@Override
	public String getScript() {
		return script.userScript;
	}
	
	@Override
	public Language getLanguage() {
		return Language.RUBY;
	}
	
	@Override
	public Fiddle withScript(String content) {
		return new JRubyFiddle(workspaceId, env, new RubyScript(content));
	}
}
