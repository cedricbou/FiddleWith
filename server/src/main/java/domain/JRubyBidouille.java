package domain;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import com.fasterxml.jackson.databind.JsonNode;

public class JRubyBidouille implements Bidouille {

	@Override
	public void execute(final JsonNode parameters) {
		  final ScriptingContainer ruby = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
		  
	      // Assign the Java objects that you want to share
		  ruby.put("json", parameters);
	  
		  // Execute a script (can be of any length, and taken from a file)
		  
		  Object result = ruby.runScriptlet(PathType.RELATIVE, "src/main/config/script.rb");
	      
		  // Use the result as if it were a Java object
		  System.out.println(result);

	}
}
