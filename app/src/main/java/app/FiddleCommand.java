package app;

import java.io.File;

import javax.ws.rs.core.Response;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.EnvironmentCommand;
import com.yammer.dropwizard.config.Environment;

import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.impl.RepositoryManager;
import fiddle.resources.Resources;
import fiddle.ruby.RubyExecutor;

public class FiddleCommand extends EnvironmentCommand<AppConfiguration> {
	/*
	 * private static final Logger LOG = LoggerFactory
	 * .getLogger(FiddleCommand.class);
	 */

	public FiddleCommand(final Service<AppConfiguration> service) {
		super(service, "fiddlewith", "Run a fiddle");
	}

	@Override
	public void configure(Subparser subparser) {
		subparser.addArgument("--repository", "-R").required(true)
				.help("The repository directory");
		subparser.addArgument("--workspace", "-W").required(true)
				.help("The workspace to use inside the repository");
		subparser.addArgument("fiddle").required(true)
				.help("The fiddle to run");

		super.configure(subparser);
	}

	@Override
	protected void run(final Environment environment,
			final Namespace namespace, final AppConfiguration configuration)
			throws Exception {

		final RepositoryManager repo = new RepositoryManager(new File(
				(String) namespace.get("repository")), environment);

		final RubyExecutor executor = new RubyExecutor();

		final FiddleId id = new FiddleId((String) namespace.get("fiddle"));

		final WorkspaceId wk = new WorkspaceId(
				(String) namespace.get("workspace"));

		final Response r = executor.execute(
				repo.resources(wk).open(ResourceFileName.VALUE)
						.or(Resources.EMPTY), id, repo.fiddles(wk).open(id)
						.get(), null);

		System.out.println(r.getEntity());
	}

}
