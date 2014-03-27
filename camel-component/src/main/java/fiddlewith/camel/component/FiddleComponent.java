package fiddlewith.camel.component;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import fiddle.execution.FiddleExecutionService;
import fiddle.repository.manager.RepositoryManager;
import fiddle.scripting.ScriptExecutor;

/**
 * Represents the component that manages {@link FiddleEndpoint}.
 */
public class FiddleComponent extends DefaultComponent {
	
	protected final RepositoryManager repository;
	protected final FiddleExecutionService fiddleRun;
	
	public FiddleComponent(final RepositoryManager repository, final ScriptExecutor executor) {
		this.repository = repository;
		this.fiddleRun = new FiddleExecutionService(repository, executor);
	}
	
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new FiddleEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
