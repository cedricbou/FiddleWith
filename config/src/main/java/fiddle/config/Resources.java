package fiddle.config;

import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.skife.jdbi.v2.DBI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import fiddle.api.registry.BaseRegistry;
import fiddle.api.registry.SelectRegistry;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.dbi.DecoratedDbi;
import fiddle.dbi.registry.DbiRegistry;
import fiddle.dbi.registry.SelectDbiRegistry;
import fiddle.dbi.registry.SimpleDbiRegistry;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.httpclient.registry.HttpRegistry;
import fiddle.httpclient.registry.SelectHttpRegistry;
import fiddle.httpclient.registry.SimpleHttpRegistry;

public class Resources {

	public static final Resources EMPTY = new Resources();

	private final SelectRegistry<DbiRegistry> dbis;

	private final SelectRegistry<HttpRegistry> https;

	private final HttpClient defaultHttp;

	private final FiddleDBIFactory factory;

	private Resources() {
		this.dbis = emptyDbis();
		this.https = emptyHttps();
		this.defaultHttp = new HttpClientBuilder().using(
				new HttpClientConfiguration()).build();
		this.factory = null;
	}

	public Resources(final Optional<Resources> global,
			final FiddleDBIFactory factory, final ResourceConfiguration config) {
		this.factory = factory;

		if (config != null) {
			if (config.getDatabases() != null) {

				final DbiRegistry local = new SimpleDbiRegistry();
				declareDbis(local, config.getDatabases());

				if (global.isPresent()) {
					this.dbis = BaseRegistry.composed(local, global.get()
							.dbis());
				} else {
					this.dbis = BaseRegistry.single(local);
				}
			} else {
				this.dbis = emptyDbis();
			}

			if (config.getHttpClients() != null) {
				if (config.getHttpClients().configured() != null) {

					final HttpRegistry local = new SimpleHttpRegistry();

					declareHttps(local, config.getHttpClients().configured());

					if (global.isPresent()) {
						this.https = BaseRegistry.composed(local, global.get()
								.https());
					} else {
						this.https = BaseRegistry.single(local);
					}
				} else {
					this.https = emptyHttps();
				}
				this.defaultHttp = new HttpClientBuilder().using(
						config.getHttpClients().getDefault()).build();

			} else {
				this.https = emptyHttps();
				this.defaultHttp = new HttpClientBuilder().using(
						new HttpClientConfiguration()).build();
			}
		} else {
			this.dbis = emptyDbis();
			this.https = emptyHttps();
			this.defaultHttp = new HttpClientBuilder().using(
					new HttpClientConfiguration()).build();
		}

	}

	public Resources(final FiddleDBIFactory factory,
			final ResourceConfiguration config) {
		this(Optional.<Resources> absent(), factory, config);
	}

	public Resources(final Resources resources, final FiddleDBIFactory factory,
			final ResourceConfiguration config) {
		this(Optional.fromNullable(resources), factory, config);
	}

	private void declareDbis(final DbiRegistry local,
			final ImmutableMap<String, DatabaseConfiguration> databases) {
		for (final Entry<String, DatabaseConfiguration> entry : databases
				.entrySet()) {
			local.declare(entry.getKey(), factory.build(entry.getValue()));
		}
	}

	private void declareHttps(final HttpRegistry local,
			final ImmutableMap<String, ConfiguredHttpConfiguration> https) {

		for (final Entry<String, ConfiguredHttpConfiguration> entry : https
				.entrySet()) {

			final ConfiguredFiddleHttpClient configuredClient = new FiddleHttpClient(
					new HttpClientBuilder().using(entry.getValue().client())
							.build()).withUrl(entry.getValue().url());

			local.declare(entry.getKey(), configuredClient);
		}

	}

	public DbiRegistry dbis() {
		return new SelectDbiRegistry(dbis);
	}

	public HttpRegistry https() {
		return new SelectHttpRegistry(https);
	}

	public FiddleHttpClient defaultHttp() {
		return new FiddleHttpClient(defaultHttp);
	}

	private SelectRegistry<DbiRegistry> emptyDbis() {
		return new SelectRegistry<DbiRegistry>() {
			@Override
			public DbiRegistry forKey(String key) {
				return new DbiRegistry() {

					@Override
					public DBI get(String key) {
						return null;
					}

					@Override
					public void declare(String key, DBI entry) {
					}

					@Override
					public boolean contains(String key) {
						return false;
					}

					@Override
					public DecoratedDbi getDecoratedDbi(String key) {
						return null;
					}
				};
			}
		};
	}

	private SelectRegistry<HttpRegistry> emptyHttps() {
		return new SelectRegistry<HttpRegistry>() {
			@Override
			public HttpRegistry forKey(String key) {
				return new HttpRegistry() {

					@Override
					public ConfiguredFiddleHttpClient get(String key) {
						return null;
					}

					@Override
					public void declare(String key,
							ConfiguredFiddleHttpClient entry) {
					}

					@Override
					public boolean contains(String key) {
						return false;
					}
				};
			}
		};
	}

}
