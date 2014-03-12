package fiddle.ruby;

import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.yammer.metrics.core.HealthCheck;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.resources.Resources;

public class RubyAnalyzer {

	private final RubyExecutor executor;
		
	private final Logger LOG = LoggerFactory.getLogger(RubyAnalyzer.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static JsonNode EMPTY_NODE;

	private final RubyMethodMatcher healthCheckMatcher = new RubyMethodMatcher("HealthCheck");

	static {
		try {
			EMPTY_NODE = MAPPER.readTree("{}");
		} catch(Exception e) {
			throw new IllegalStateException("FATAL : failed to create the empty JSON node constant");
		}
	}
	
	private static class RubyHealthCheck extends HealthCheck {
		
		private final RubyExecutor executor;
		
		private final FiddleId id;
		private final Fiddle fiddle;
		private final String method;
		
		public RubyHealthCheck(final RubyExecutor executor, final FiddleId id, final Fiddle fiddle, final String method) {
			super("fiddle/with/" + id.toString() + "/" + method);
			this.executor = executor;
			this.id = id;
			this.fiddle = fiddle;
			this.method = method;
		}
		
		@Override
		protected Result check() throws Exception {
			final Response response = executor.executeMethodIn(Resources.EMPTY, id, fiddle, method, EMPTY_NODE);
			if(response.getStatus() >= 200 && response.getStatus() < 300) {
				return Result.healthy();
			}
			else {
				return Result.unhealthy(response.getEntity().toString());
			}
		}
		
	}
	
	public RubyAnalyzer(final RubyExecutor executor) {
		this.executor = executor;
	}
	
	public List<HealthCheck> healthchecks(final FiddleId id, final Fiddle fiddle) {
		LOG.debug("Looking for healthcheck in {}", id);
		

		return Lists.transform(healthCheckMatcher.methodsFor(fiddle), new Function<String, HealthCheck>() {
			@Override
			public HealthCheck apply(final String method) {
				return new RubyHealthCheck(executor, id, fiddle, method);
			}
		});
	}
}
