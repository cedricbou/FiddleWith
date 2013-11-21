package fiddle.ruby;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.FiddleResponseBuilder;
import fiddle.ruby.json.serializer.RubyJacksonModule;

public class RubyExecutor {

	private static Logger LOG = LoggerFactory.getLogger(RubyExecutor.class);

	private final ScriptingContainer ruby = new ScriptingContainer(
			LocalContextScope.THREADSAFE, LocalVariableBehavior.TRANSIENT);

	private final FiddleResponseBuilder responseBuilder = new FiddleResponseBuilder(
			mapper);

	private final static String includer = 
		"require 'prefiddle.rb'" + "\n" 
			+ "r = __r" + "\n"
			+ "d = JsonSugar.new(__d)" + "\n";
		
	
	private void initRuby() {
		final List<String> paths = ruby.getLoadPaths();
		
		if(paths.size() > 0 && paths.get(paths.size() - 1).startsWith("fiddle")) {
			return;
		}
		
		final List<String> newPaths = new ArrayList<String>(paths.size() + 1);
		newPaths.add("fiddle/ruby/lib");
		newPaths.addAll(paths);
		
		ruby.setLoadPaths(newPaths);
	}
	
	public Response execute(final FiddleId id, final Fiddle fiddle, final JsonNode data) {
		initRuby();
		
		ruby.clear();
		ruby.put("__r", responseBuilder);
		ruby.put("__d", data);
		
		LOG.debug("will execute ruby script for fiddle {} with data {}", id,
				data);

		Object result = ruby.runScriptlet(includer + fiddle.content);

		LOG.debug("fiddle execution result is {}", result);

		final Response r;

		if (result instanceof Response) {
			r = (Response) result;
		} else {
			r = responseBuilder.ok(result);
		}

		LOG.debug("response status is {} with entity {}", r.getStatus(),
				r.getEntity());

		return r;
	}

	private static final ObjectMapper mapper = new ObjectMapper();
	{
		mapper.registerModule(RubyJacksonModule.MODULE);
	}
}
