package fiddle.resources;

import org.apache.camel.CamelContext;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.resources.impl.CamelResources;

public class FallbackResources implements Resources {

	private final Resources main;
	private final Resources fallback;

	public FallbackResources(final Resources main, final Resources fallback) {
		this.main = main;
		this.fallback = fallback;
	}

	public DecoratedDbi dbi(final String id) {
		return Optional.fromNullable(main.dbi(id)).or(
				new Supplier<DecoratedDbi>() {
					@Override
					public DecoratedDbi get() {
						return fallback.dbi(id);
					}
				});
	};

	@Override
	public FiddleHttpClient http() {
		return Optional.fromNullable(main.http()).or(
				new Supplier<FiddleHttpClient>() {
					public FiddleHttpClient get() {
						return fallback.http();
					}
				});
	}

	@Override
	public ConfiguredFiddleHttpClient http(final String id) {
		return Optional.fromNullable(main.http(id)).or(
				new Supplier<ConfiguredFiddleHttpClient>() {
					public ConfiguredFiddleHttpClient get() {
						return fallback.http(id);
					}
				});
	}

	@Override
	public CamelContext camel() {
		return CamelResources.camel;
	}
}
