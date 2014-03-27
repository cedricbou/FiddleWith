package fiddlewith.camel.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;

/**
 * Represents a Fiddle endpoint.
 */
public class FiddleEndpoint extends DefaultEndpoint {

	protected final WorkspaceId workspaceId;
	protected final FiddleId fiddleId;
	protected final FiddleComponent component;
	
	private String uriWorkspace(final String uri) {
		return uri.split("\\:")[1];
	}
	
	private String uriFiddle(final String uri) {
		return uri.split("\\:")[2].split("\\?")[0];
	}
	
    public FiddleEndpoint(String uri, FiddleComponent component) {
        super(uri, component);
        
        this.workspaceId = new WorkspaceId(uriWorkspace(uri));
        this.fiddleId = new FiddleId(uriFiddle(uri));
        this.component = component;
    }

    public Producer createProducer() throws Exception {
        return new FiddleProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new FiddleConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }
}
