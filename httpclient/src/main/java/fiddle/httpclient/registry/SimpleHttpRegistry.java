package fiddle.httpclient.registry;

import fiddle.api.registry.BaseRegistry;
import fiddle.httpclient.ConfiguredFiddleHttpClient;

public class SimpleHttpRegistry extends BaseRegistry<ConfiguredFiddleHttpClient> implements HttpRegistry {

	// private final Logger LOG = LoggerFactory.getLogger(SimpleHttpRegistry.class);
	
	public SimpleHttpRegistry() {
		super();
	}
}
