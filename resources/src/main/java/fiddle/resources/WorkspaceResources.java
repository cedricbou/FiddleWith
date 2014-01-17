package fiddle.resources;

import com.github.mustachejava.Mustache;
import com.yammer.dropwizard.config.Environment;

import fiddle.api.TemplateId;
import fiddle.config.ResourceConfiguration;
import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.repository.Repository;
import fiddle.resources.http.DbiResources;
import fiddle.resources.http.HttpResources;

public class WorkspaceResources implements Resources {

	private final HttpResources https;
	private final DbiResources dbis;

	public WorkspaceResources(
			final Repository<Mustache, String, TemplateId> templates,
			final ResourceConfiguration config, final Environment env) {
		
		this.https = new HttpResources(templates, config.getHttpClients());
		this.dbis = new DbiResources(config, env);
	}

	@Override
	public DecoratedDbi dbi(String id) {
		return dbis.dbi(id);
	}

	public FiddleHttpClient http() {
		return https.http();
	};

	@Override
	public ConfiguredFiddleHttpClient http(String id) {
		return https.http(id);
	}
}
