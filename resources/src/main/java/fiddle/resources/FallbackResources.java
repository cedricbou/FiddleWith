package fiddle.resources;

import com.google.common.base.Optional;

import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;

public class FallbackResources implements Resources {

	private final Resources main;
	private final Resources fallback;

	public FallbackResources(final Resources main, final Resources fallback) {
		this.main = main;
		this.fallback = fallback;
	}

	public DecoratedDbi dbi(String id) {
		return Optional.fromNullable(main.dbi(id)).or(fallback.dbi(id));
	};

	@Override
	public FiddleHttpClient http() {
		return Optional.fromNullable(main.http()).or(fallback.http());
	}

	@Override
	public ConfiguredFiddleHttpClient http(String id) {
		return Optional.fromNullable(main.http(id)).or(fallback.http(id));
	}
}
