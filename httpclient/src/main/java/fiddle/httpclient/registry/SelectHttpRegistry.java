package fiddle.httpclient.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiddle.api.registry.SelectRegistry;
import fiddle.httpclient.ConfiguredFiddleHttpClient;

public class SelectHttpRegistry implements HttpRegistry {

	private final SelectRegistry<HttpRegistry> select;
	
	private final Logger LOG = LoggerFactory.getLogger(SelectHttpRegistry.class);

	public SelectHttpRegistry(final SelectRegistry<HttpRegistry> select) {
		this.select = select;
	}

	@Override
	public boolean contains(String key) {
		return select.forKey(key).contains(key);
	}

	@Override
	public void declare(String key, ConfiguredFiddleHttpClient entry) {
		throw new RuntimeException("declare not supported for Select registry");
	}

	@Override
	public ConfiguredFiddleHttpClient get(String key) {
		LOG.debug("get default http client {}", key);

		return select.forKey(key).get(key);
	}
}
