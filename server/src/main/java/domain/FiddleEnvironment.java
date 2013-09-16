package domain;

import java.io.File;

import org.apache.http.client.HttpClient;

import domain.client.FiddleHttpClient;
import domain.repo.FiddleRepository;
import domain.ruby.RubyEnv;
import domain.sql.FiddleSqlRegistry;
import domain.template.FiddleMustacheRenderer;

public class FiddleEnvironment {
	
	public final FiddleSqlRegistry sql;
	public final RubyEnv ruby;
	public final FiddleRepository repository;
	public final FiddleMustacheRenderer templates;
	public final FiddleHttpClient httpClient;
	
	public FiddleEnvironment(final FiddleSqlRegistry sql, final File fiddleRepository, final HttpClient defaultHttpClient) {
		this.sql = sql;
		this.ruby = new RubyEnv(sql);
		this.repository = new FiddleRepository(fiddleRepository, ruby);
		this.templates = new FiddleMustacheRenderer(fiddleRepository);
		this.httpClient = new FiddleHttpClient(templates, defaultHttpClient);
	}

}
