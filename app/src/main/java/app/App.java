package app;

import java.io.File;

import resources.FiddleEditorResource;
import resources.FiddleResource;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.metrics.core.HealthCheck;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.password.PasswordManager;
import fiddle.repository.manager.RepositoryManager;
import fiddle.ruby.RubyAnalyzer;
import fiddle.ruby.RubyExecutor;

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
		
		repoManager.resourcesManager().preload();
		
		final RubyExecutor executor = new RubyExecutor();
		
		env.addResource(new FiddleResource(repoManager, executor));
		env.addResource(new FiddleEditorResource());
		
		for(final HealthCheck hc : healthchecks(repoManager, new RubyAnalyzer(executor))) {
			env.addHealthCheck(hc);
		}
	}
	
	private ImmutableList<HealthCheck> healthchecks(final RepositoryManager repo, final RubyAnalyzer analyzer) {
		final Builder<HealthCheck> builder = ImmutableList.<HealthCheck>builder();
		
		for(final WorkspaceId wId : repo.fiddlesManager().workspaces()) {
			for(final FiddleId id : repo.fiddles(wId).ids()) {
				final Optional<Fiddle> fiddle = repo.fiddles(wId).open(id);
				if(fiddle.isPresent()) {
					builder.addAll(analyzer.healthchecks(id, fiddle.get()));
				}
			}
		}
		
		return builder.build();
	}

}
