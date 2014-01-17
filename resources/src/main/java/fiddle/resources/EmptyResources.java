package fiddle.resources;

import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;

public class EmptyResources implements Resources {

	@Override
	public DecoratedDbi dbi(String id) {
		return null;
	}

	@Override
	public FiddleHttpClient http() {
		return null;
	}

	@Override
	public ConfiguredFiddleHttpClient http(String id) {
		return null;
	}
}
