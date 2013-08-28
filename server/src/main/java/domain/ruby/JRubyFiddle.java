package domain.ruby;

import java.io.File;

import javax.ws.rs.core.Response;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.Fiddle;
import domain.FiddleEnvironment;
import domain.FiddleResponse;
import domain.json.serializer.RubyJacksonModule;

public class JRubyFiddle implements Fiddle {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	{
		mapper.registerModule(RubyJacksonModule.MODULE);
	}
	
	private final FiddleEnvironment env;
	
	public JRubyFiddle(final FiddleEnvironment env) {
		this.env = env;
	}

	@Override
	public Response execute(final JsonNode data) {

		final ScriptingContainer ruby = new ScriptingContainer(
				LocalVariableBehavior.TRANSIENT);

		// Assign the Java objects that you want to share
		ruby.put("response", new FiddleResponse(mapper));
		ruby.put("json", data);
		ruby.put("sql", env.ruby.sql);

		// Execute a script (can be of any length, and taken from a file)

		Object result = ruby.runScriptlet(new RubyScript(new File(
				"./src/main/config/tryit.rb")).script);

		// Use the result as if it were a Java object
		System.out.println(result);

		if (result instanceof Response) {
			return (Response) result;
		} else {
			return Response.status(500)
					.tag("Script did not return response object").build();
		}
	}
}
