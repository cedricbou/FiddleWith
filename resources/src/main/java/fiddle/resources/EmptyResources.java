package fiddle.resources;

import org.apache.camel.CamelContext;

import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.resources.impl.CamelResources;

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
	
	@Override
	public CamelContext camel() {
		return CamelResources.camel;
	}
}
