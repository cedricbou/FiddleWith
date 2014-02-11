package app;

import java.io.File;

import resources.FiddleResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import fiddle.password.PasswordManager;
import fiddle.repository.manager.RepositoryManager;
import fiddle.resources.builder.DropwizardBuilders;

public class App extends Service<AppConfiguration> {
	
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.setName("fiddlewith.it");
		bootstrap.addCommand(new FiddleCommand(this));
		bootstrap.addCommand(new PasswordCommand());
		bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
		bootstrap.addBundle(new ViewBundle());
	}
	
	@Override
	public void run(final AppConfiguration config, final Environment env) throws Exception {
		final PasswordManager passwords = config.getUsers();

		env.addProvider(new BasicAuthProvider<PasswordManager.UserConfiguration>(new AppAuthenticator(passwords), "fiddle with!"));
		
		final RepositoryManager repoManager = new RepositoryManager(new File(config.getRepository()), env);
		env.addResource(new FiddleResource(repoManager));
	}
}
