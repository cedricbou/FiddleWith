package app;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import ressources.FiddleResource;
import ressources.TemplateResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.views.ViewBundle;

import domain.FiddleEnvironment;
import domain.User;

public class MainService extends Service<AppConfiguration> {

	public static void main(String[] args) throws Exception {
		new MainService().run(args);
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.setName("fiddle-with");
		bootstrap.addCommand(new cli.RubyCommand());
		bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
		bootstrap.addBundle(new ViewBundle());
	}

	@Override
	public void run(AppConfiguration config, Environment env) throws Exception {
		final DBIFactory factory = new DBIFactory();

		final HttpClient httpClient = new HttpClientBuilder().using(config.defaultHttp())
                .build();

		env.addProvider(new BasicAuthProvider<User>(new AdminAuthenticator(), "fiddle"));
		
		final FiddleEnvironment fidEnv = new FiddleEnvironment(
				config.sql(env, factory),
				config.http(),
				config.fiddleRepository(),
				(DefaultHttpClient)httpClient);
		
		env.addResource(new FiddleResource(fidEnv));
		env.addResource(new TemplateResource(fidEnv));
	}
}
