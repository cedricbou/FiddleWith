package domain;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;

import domain.http.FiddleHttpClient;
import domain.http.FiddleHttpRegistry;
import domain.http.HttpRegistry;
import domain.repo.FiddleRepository;
import domain.ruby.RubyEnv;
import domain.sql.FiddleSqlRegistry;
import domain.template.FiddleMustacheRenderer;

public class FiddleEnvironment {
	
	public final FiddleSqlRegistry sql;
	public final FiddleHttpRegistry http;
	public final RubyEnv ruby;
	public final FiddleRepository repository;
	public final FiddleMustacheRenderer templates;
	public final FiddleHttpClient defaultHttp;
	
	public FiddleEnvironment(final FiddleSqlRegistry sql, final HttpRegistry http, final File fiddleRepository, final DefaultHttpClient defaultHttp) {
		this.sql = sql;
		this.repository = new FiddleRepository(fiddleRepository, this);
		this.templates = new FiddleMustacheRenderer(fiddleRepository);
		this.http = new FiddleHttpRegistry(http, templates);
		this.defaultHttp = new FiddleHttpClient(templates, defaultHttp);
		this.ruby = new RubyEnv(sql, this.defaultHttp, this.http);
	}

}
