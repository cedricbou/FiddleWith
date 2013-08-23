package app;

import ressources.BidouilleResource;
import cli.AppConfiguration;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import domain.BidouilleRepository;

public class MainService extends Service<AppConfiguration> {

	public static void main(String[] args) throws Exception {
		new MainService().run(args);
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {
		bootstrap.setName("net-bidouilles");
		bootstrap.addCommand(new cli.RubyCommand());
		bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
		bootstrap.addBundle(new ViewBundle());
	}

	@Override
	public void run(AppConfiguration config, Environment env) throws Exception {
		env.addResource(new BidouilleResource(new BidouilleRepository()));
	}
}
