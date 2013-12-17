package app;

import java.io.File;

import org.skife.jdbi.v2.DBI;

import resources.FiddleResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.views.ViewBundle;

import fiddle.config.FiddleDBIFactory;
import fiddle.config.Resources;
import fiddle.repository.Repository;

public class App extends Service<AppConfiguration> {
	
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.setName("fiddlewith.it");
		bootstrap.addCommand(new FiddleCommand(this));
		bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
		bootstrap.addBundle(new ViewBundle());
	}
	
	@Override
	public void run(final AppConfiguration config, final Environment env) throws Exception {

		final Repository repository = new Repository(new File(config.getRepository()));

		final DBIFactory dbiFactory = new DBIFactory();
		final FiddleDBIFactory fiddleDbiFactory = new FiddleDBIFactory() {

			@Override
			public DBI build(DatabaseConfiguration config) {
				try {
					return dbiFactory.build(env, config, "db");
				} catch (ClassNotFoundException cnfe) {
					throw new RuntimeException(
							"failed to build dbi with provided factory", cnfe);
				}
			}
		};

		final Resources globalResources = new Resources(fiddleDbiFactory,
				config.getGlobalResourceConfiguration());

		env.addResource(new FiddleResource(globalResources, repository, fiddleDbiFactory));
	}
}
