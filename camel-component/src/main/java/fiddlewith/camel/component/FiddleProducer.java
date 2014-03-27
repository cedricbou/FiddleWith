package fiddlewith.camel.component;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Fiddle producer.
 */
public class FiddleProducer extends DefaultProducer {
	private static final Logger LOG = LoggerFactory
			.getLogger(FiddleProducer.class);

	private FiddleEndpoint endpoint;

	public FiddleProducer(FiddleEndpoint endpoint) {
		super(endpoint);
		this.endpoint = endpoint;
	}

	public void process(Exchange exchange) throws Exception {
		final Response r = endpoint.component.fiddleRun.doFiddle(
				endpoint.workspaceId, endpoint.fiddleId, exchange
						.getIn().getBody());
		
		exchange.getOut().setBody(r.getEntity());
	}

}
