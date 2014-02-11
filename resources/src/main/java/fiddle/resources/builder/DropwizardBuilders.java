package fiddle.resources.builder;

import org.skife.jdbi.v2.DBI;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.httpclient.TemplateStringBuilder;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;

public class DropwizardBuilders {
	
	public static DropwizardBuilders builders(final Environment env, final RepositoryManager<Repository<Mustache, String, TemplateId>> templates) {
		return new DropwizardBuilders(env, templates);
	}

	private final RepositoryManager<Repository<Mustache, String, TemplateId>> templates;
	private final Environment env;
	
	public DropwizardBuilders(final Environment env, final RepositoryManager<Repository<Mustache, String, TemplateId>> templates) {
		this.env = env;
		this.templates = templates;
	}
	
	public DbiResourceBuilder dbiBuilder() {
		return new DropWizardDbiResourceBuilder(env, new DBIFactory());
	}
	
	public HttpResourceBuilder httpBuilder() {
		return new DropWizardHttpResourceBuilder(templates);
	}
	
	private static class DropWizardHttpResourceBuilder implements HttpResourceBuilder {
		
		private final RepositoryManager<Repository<Mustache, String, TemplateId>> templates;
		
		public DropWizardHttpResourceBuilder(final RepositoryManager<Repository<Mustache, String, TemplateId>> templates) {
			this.templates = templates;
		}
		
		@Override
		public ConfiguredFiddleHttpClient buildFromConfig(final WorkspaceId wId, final String id,
				ConfiguredHttpConfiguration config) {
			
			final Repository<Mustache, String, TemplateId> templates = this.templates.repo(wId);
			
			// Constuct with URL
			final ConfiguredFiddleHttpClient client = new FiddleHttpClient(
					new HttpClientBuilder().using(config.client()).build())
					.withUrl(config.url());

			final ConfiguredFiddleHttpClient withHeaders;
			
			if(config.headers() != null) {
				withHeaders = client.withHeaders(config.headers());
			}
			else {
				withHeaders = client;
			}
			
			final ConfiguredFiddleHttpClient withBody;

			if (config.body() != null) {
				final Optional<Mustache> body = templates.open(new TemplateId(
						config.body()));

				if (body.isPresent()) {
					withBody = withHeaders
							.withBody(new TemplateStringBuilder.MustacheTemplateStringBuilder(body
									.get()));
				} else {
					withBody = withHeaders.withBody(config
							.body());
				}
			} else {
				withBody = withHeaders;
			}
			
			return withBody;
		}
	}
	
	private static class DropWizardDbiResourceBuilder implements DbiResourceBuilder {
		private final DBIFactory dbiFactory;
		private final Environment env;
				
		public DropWizardDbiResourceBuilder(final Environment env, final DBIFactory dbiFactory) {
			this.env = env;
			this.dbiFactory = dbiFactory;
		}
		
		@Override
		public DBI buildDbiFromConfig(String id, DatabaseConfiguration config) {
			try {
				return dbiFactory.build(env, config, id);
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException(
						"failed to build dbi with provided factory", cnfe);
			}
		}
	}
}
