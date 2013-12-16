package app;

import java.io.File;

import javax.ws.rs.core.Response;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.cli.ConfiguredCommand;
import com.yammer.dropwizard.config.Bootstrap;

import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;


public class FiddleCommand extends ConfiguredCommand<AppConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FiddleCommand.class);
	
	public FiddleCommand() {
		super("fiddlewith", "Run a fiddle");
	}
	
	@Override
	public void configure(Subparser subparser) {
		subparser.addArgument("--repository", "-R").required(true).help("The repository directory");
		subparser.addArgument("--workspace", "-W").required(true).help("The workspace to use inside the repository");
		subparser.addArgument("fiddle").required(true).help("The fiddle to run");
	
		super.configure(subparser);
	}
	
  @Override
    protected void run(Bootstrap<AppConfiguration> bootstrap,
                       Namespace namespace,
                       AppConfiguration configuration) throws Exception {

	  final Repository repo = new Repository(new File((String)namespace.get("repository")));
	
	  final RubyExecutor executor = new RubyExecutor();
	  
	  final FiddleId id = new FiddleId((String)namespace.get("fiddle"));
	  final WorkspaceId wk = new WorkspaceId((String)namespace.get("workspace"));
	  
	  final Response r = executor.execute(
			 id,
			 repo.fiddles(wk).open(id).get(),
			 null);
	  
	  System.out.println(r.getEntity());
    }
	
}
