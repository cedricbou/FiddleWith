package fiddle.scripting;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.resources.Resources;

public class ScriptRouteBuilder extends RouteBuilder {

	private final ScriptExecutor executor;

	private final FiddleId id;
	private final Fiddle fiddle;
	private final String method;
	
	private final CamelContext camel;

	public ScriptRouteBuilder(final ScriptExecutor executor, final FiddleId id,
			final Fiddle fiddle, final String method, final CamelContext camel) {
		super();
		this.executor = executor;
		this.id = id;
		this.fiddle = fiddle;
		this.method = method;
		this.camel = camel;
	}

	@Override
	public void configure() throws Exception {
		executor.rawExecuteMethodIn(Resources.EMPTY, id, fiddle, method, this);
	}
}