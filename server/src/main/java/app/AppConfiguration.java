package app;

import java.io.File;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;

import domain.exceptions.SqlException;
import domain.http.HttpRegistry;
import domain.http.HttpRegistry.ConfiguredHttpClient;
import domain.sql.FiddleSqlRegistry;

public class AppConfiguration extends Configuration {

	@Valid
	@NotNull
	@JsonProperty
	private ImmutableMap<String, DatabaseConfiguration> sql;

	@Valid
	@NotNull
	@JsonProperty
	private ImmutableMap<String, FiddleHttpConfiguration> httpClients = ImmutableMap.of();

	@Valid
	@NotNull
	@JsonProperty
	private File fiddleRepository = new File(System.getProperty("user.home"));

	@Valid
	@NotNull
	@JsonProperty
	private HttpClientConfiguration defaultHttp = new HttpClientConfiguration();

	public HttpRegistry http() {
		final HttpRegistry registry = new HttpRegistry();

		for (final String key : httpClients.keySet()) {
			final HttpClient httpClient = new HttpClientBuilder().using(
					httpClients.get(key).config()).build();

			registry.declare(key, new ConfiguredHttpClient((DefaultHttpClient)httpClient, httpClients.get(key).uri(), httpClients.get(key).template()));
		}
		
		return registry;
	}

	public FiddleSqlRegistry sql(final Environment env, final DBIFactory factory) {
		final FiddleSqlRegistry registry = new FiddleSqlRegistry();

		for (final String key : sql.keySet()) {
			try {
				final DBI jdbi = factory.build(env, sql.get(key), "mysql" /*
																		 * TODO:
																		 * hard
																		 * coded
																		 * piece
																		 * to
																		 * remove
																		 */);

				registry.declare(key, jdbi);
			} catch (ClassNotFoundException e) {
				throw new SqlException(
						"Failed to craft jdbi instance during configuration initialisation",
						e);
			}
		}
		return registry;
	}

	public File fiddleRepository() {
		return fiddleRepository;
	}

	public HttpClientConfiguration defaultHttp() {
		return defaultHttp;
	}

}
