package fiddle.scripting;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.metrics.core.HealthCheck;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.resources.Resources;

public class ScriptHealthCheck extends HealthCheck {
		
		private final ScriptExecutor executor;
		
		private final FiddleId id;
		private final Fiddle fiddle;
		private final String method;

		private static final ObjectMapper MAPPER = new ObjectMapper();
		
		private static JsonNode EMPTY_NODE;

		static {
			try {
				EMPTY_NODE = MAPPER.readTree("{}");
			} catch(Exception e) {
				throw new IllegalStateException("FATAL : failed to create the empty JSON node constant");
			}
		}
		
		public ScriptHealthCheck(final ScriptExecutor executor, final FiddleId id, final Fiddle fiddle, final String method) {
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