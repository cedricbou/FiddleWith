package cli;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.config.Bootstrap;

import domain.Person;


public class RubyCommand extends ConfiguredCommand<AppConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RubyCommand.class);
	
	public RubyCommand() {
		super("test-ruby", "Try to run a ruby script");
	}
	
	@Override
	public void configure(Subparser subparser) {
		super.configure(subparser);
	}
	
  @Override
    protected void run(Bootstrap<AppConfiguration> bootstrap,
                       Namespace namespace,
                       AppConfiguration configuration) throws Exception {

	  final ScriptingContainer ruby = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
	  
      // Assign the Java objects that you want to share
	  ruby.put("person", new Person("Cedric", "Boufflers"));
  
	  // Execute a script (can be of any length, and taken from a file)
	  Object result = ruby.runScriptlet("5.times { puts person.name }");
      
	  // Use the result as if it were a Java object
	  System.out.println(result);
    }
	
}
