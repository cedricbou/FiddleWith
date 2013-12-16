package app;

import java.io.File;

import javax.ws.rs.core.Response;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.skife.jdbi.v2.DBI;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.EnvironmentCommand;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.FiddleDBIFactory;
import fiddle.config.Resources;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

public class FiddleCommand extends EnvironmentCommand<AppConfiguration> {
/*	private static final Logger LOG = LoggerFactory
			.getLogger(FiddleCommand.class); */

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

		final Repository repo = new Repository(new File(
				(String) namespace.get("repository")));

		final RubyExecutor executor = new RubyExecutor();

		final FiddleId id = new FiddleId((String) namespace.get("fiddle"));
		final WorkspaceId wk = new WorkspaceId(
				(String) namespace.get("workspace"));

		final DBIFactory dbiFactory = new DBIFactory();

		final FiddleDBIFactory fiddleDbiFactory = new FiddleDBIFactory() {

			@Override
			public DBI build(DatabaseConfiguration config) {
				try {
					return dbiFactory.build(environment, config, "db");
				} catch (ClassNotFoundException cnfe) {
					throw new RuntimeException(
							"failed to build dbi with provided factory", cnfe);
				}
			}
		};

		final Resources globalResources = new Resources(fiddleDbiFactory,
				configuration.getGlobalResourceConfiguration());

		final Response r = executor.execute(
				repo.resources(globalResources, fiddleDbiFactory, wk), id, repo
						.fiddles(wk).open(id).get(), null);

		System.out.println(r.getEntity());
	}

}
