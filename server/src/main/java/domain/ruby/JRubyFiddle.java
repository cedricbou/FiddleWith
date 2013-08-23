package domain.ruby;

import java.io.File;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.fasterxml.jackson.databind.JsonNode;

import domain.Fiddle;

public class JRubyFiddle implements Fiddle {

	@Override
	public void execute(final JsonNode parameters) {
		final ScriptingContainer ruby = new ScriptingContainer(
				LocalVariableBehavior.PERSISTENT);

		// Assign the Java objects that you want to share
		ruby.put("json", parameters);

		// Execute a script (can be of any length, and taken from a file)

		Object result = ruby.runScriptlet(new RubyScript(new File(
				"./src/main/config/tryit.rb")).script);

		// Use the result as if it were a Java object
		System.out.println(result);

	}
}
