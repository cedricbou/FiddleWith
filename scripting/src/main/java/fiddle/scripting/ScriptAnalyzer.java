package fiddle.scripting;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.yammer.metrics.core.HealthCheck;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;

// TODO: move analyzer in a new scripting project. scripting will depend on ruby and will provide factory for ruby analyzer.
// This can be use in app and should resolve circular dependencies..

public class ScriptAnalyzer {

	private final ScriptExecutor executor;
		
	private final Logger LOG = LoggerFactory.getLogger(ScriptAnalyzer.class);
	
	private final ScriptMethodMatcher healthCheckMatcher;

	private final ScriptMethodMatcher camelRouteMatcher;
	
	private final CamelContext camel;
		
	public ScriptAnalyzer(final ScriptExecutor executor, final ScriptMethodMatcher healthCheckMatcher, final ScriptMethodMatcher camelRouteMatcher, final CamelContext camel) {
		this.executor = executor;
		this.healthCheckMatcher = healthCheckMatcher;
		this.camelRouteMatcher = camelRouteMatcher;
		this.camel = camel;
	}
	
	public List<HealthCheck> healthchecks(final FiddleId id, final Fiddle fiddle) {
		LOG.info("Looking for healthcheck in {}", id);

		return Lists.transform(healthCheckMatcher.methodsFor(fiddle), new Function<String, HealthCheck>() {
			@Override
			public HealthCheck apply(final String method) {
				return new ScriptHealthCheck(executor, id, fiddle, method);
			}
		});
	}
	
	public List<RouteBuilder> camelRoutes(final FiddleId id, final Fiddle fiddle) {
		LOG.info("Looking for camel routes in {}", id);
		
		return Lists.transform(camelRouteMatcher.methodsFor(fiddle), new Function<String, RouteBuilder>() {
			@Override
			public RouteBuilder apply(String method) {
				return new ScriptRouteBuilder(executor, id, fiddle, method, camel);
			}
		});
	}
	
	
}
