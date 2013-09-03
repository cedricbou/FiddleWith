package domain.ruby;

import javax.ws.rs.core.Response;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.Fiddle;
import domain.FiddleResponse;
import domain.json.serializer.RubyJacksonModule;

public class JRubyFiddle implements Fiddle {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	{
		mapper.registerModule(RubyJacksonModule.MODULE);
	}
	
	private final RubyEnv env;
	private final RubyScript script;
	
	public JRubyFiddle(final RubyEnv env, final RubyScript script) {
		this.env = env;
		this.script = script;
	}

	@Override
	public Response execute(final JsonNode data) {

		final ScriptingContainer ruby = new ScriptingContainer(
				LocalVariableBehavior.TRANSIENT);

		// Assign the Java objects that you want to share
		ruby.put("response", new FiddleResponse(mapper));
		ruby.put("json", data);
		ruby.put("sql", env.sql);

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
}
