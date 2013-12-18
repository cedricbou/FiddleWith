package app;

import java.util.List;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import com.yammer.dropwizard.cli.Command;
import com.yammer.dropwizard.config.Bootstrap;

import fiddle.password.PasswordHash;

public class PasswordCommand extends Command {
	
	public PasswordCommand() {
		super("hash", "Generates hash to paste in configuration for the given password");
	}
	
	@Override
	public void configure(Subparser subparser) {
		subparser.addArgument("password").help("The password to hash").required(true);
		subparser.addArgument("-t", "--tags").dest("tags").help("The user allowed tags, if passed the output will be the full User yaml snippet.").nargs("*").required(false);
		subparser.addArgument("-u", "--user").dest("user").help("The user login, if passed the output will be the full User yaml snippet.").required(false);
	}
	
	@Override
	public void run(Bootstrap<?> bootstrap, Namespace namespace)
			throws Exception {
		final String password = namespace.getString("password");
		
		final String user = namespace.getString("user");
		final List<String> tags = namespace.<String>getList("tags");
		
		final String hash = PasswordHash.createHash(password);
		
		if(user != null) {
			System.out.println("users:");
			System.out.println("   -");
			System.out.println("      user: " + user);
			if(tags != null) {
				System.out.println("      tags: ");
				for(final String tag : tags) {
					System.out.println("         - " + tag);
				}
			}
			System.out.println("      hash: " + hash);
		}
		else {
			System.out.println(hash);
		}
	}

}
