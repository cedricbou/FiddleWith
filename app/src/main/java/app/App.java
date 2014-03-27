package app;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import resources.FiddleEditorResource;
import resources.FiddleResource;

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
import fiddle.repository.manager.FiddlesRepositoryManager.FiddleVisitor;
import fiddle.repository.manager.RepositoryManager;
import fiddle.resources.impl.CamelResources;
import fiddle.ruby.RubyExecutor;
import fiddle.ruby.RubyMethodMatcher;
import fiddle.scripting.ScriptAnalyzer;
import fiddle.scripting.ScriptExecutor;
import fiddlewith.camel.component.FiddleComponent;

public class App extends Service<AppConfiguration> {

	private final CamelContext camel = CamelResources.camel;

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
	public void run(final AppConfiguration config, final Environment env)
			throws Exception {
		final PasswordManager passwords = config.getUsers();

		env.addProvider(new BasicAuthProvider<PasswordManager.UserConfiguration>(
				new AppAuthenticator(passwords), "fiddle with!"));

		final RepositoryManager repoManager = new RepositoryManager(new File(
				config.getRepository()), env);

		repoManager.resourcesManager().preload();

		final ScriptExecutor executor = new RubyExecutor();

		camel.addComponent("fiddle", new FiddleComponent(repoManager, executor));

		final ScriptAnalyzer rubyAnalyzer = new ScriptAnalyzer(executor,
				new RubyMethodMatcher("HealthCheck"), new RubyMethodMatcher(
						"CamelRoute"), camel);

		final List<RouteBuilder> routeBuilders = new LinkedList<RouteBuilder>();
		final List<HealthCheck> healthChecks = new LinkedList<HealthCheck>();

		repoManager.fiddlesManager().visit(new FiddleVisitor() {
			@Override
			public void visit(WorkspaceId wid, FiddleId fid, Fiddle fiddle) {
				healthChecks.addAll(rubyAnalyzer.healthchecks(fid, fiddle));

				routeBuilders.addAll(rubyAnalyzer.camelRoutes(fid, fiddle));
			}
		});

		for (final HealthCheck hc : healthChecks) {
			env.addHealthCheck(hc);
		}

		for (final RouteBuilder rb : routeBuilders) {
			camel.addRoutes(rb);
		}

		env.addResource(new FiddleResource(repoManager, executor));
		env.addResource(new FiddleEditorResource());

		camel.start();
	}

}
