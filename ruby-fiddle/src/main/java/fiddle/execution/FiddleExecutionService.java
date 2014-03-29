package fiddle.execution;

import java.io.IOException;
import java.math.BigDecimal;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.yammer.metrics.core.TimerContext;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.manager.RepositoryManager;
import fiddle.resources.Resources;
import fiddle.scripting.ScriptExecutor;

public class FiddleExecutionService {

	private final FiddleMetrics metrics = new FiddleMetrics();

	private final RepositoryManager repository;
	private final ScriptExecutor executor;
	
	public FiddleExecutionService(RepositoryManager repository, final ScriptExecutor executor ) {
		this.repository = repository;
		this.executor = executor;
	}
	
	private Optional<Fiddle> findFiddle(final WorkspaceId wId,
			final FiddleId fId) {
		return repository.fiddles(wId).open(fId);
	}

	
	private JsonNode singleValueToUnderscore(final Object value) {
		final ObjectNode node = JsonNodeFactory.instance.objectNode();
		
		if(value == null) {
			return node;
		}
		
		if(value instanceof Long) {
			node.put("_", (Long)value);
		}
		else if(value instanceof Boolean) {
			node.put("_", (Boolean)value);
		}
		else if(value instanceof BigDecimal) {
			node.put("_", (BigDecimal)value);
		}
		else if(value instanceof Integer) {
			node.put("_", (Integer)value);
		}
		else if(value instanceof Float) {
			node.put("_", (Float)value);
		}
		else if(value instanceof Double) {
			node.put("_", (Double)value);
		}
		else if(value instanceof String) {
			node.put("_", (String)value);
		}
		else {
			node.put("_", value.toString());
		}
		
		return node;
	}
	
	public Response doFiddle(final WorkspaceId workspaceId,
			final FiddleId fiddleId, final Object params)
			throws JsonProcessingException, IOException  {
		
		final TimerContext context = metrics.timer(workspaceId, fiddleId)
				.time();

		final JsonNode json;
		
		if(params instanceof String) {
			JsonNode parsed;
			try {
				final ObjectMapper mapper = new ObjectMapper();
				parsed = mapper.readTree((String)params);
			}
			catch(Exception e) {
				parsed = singleValueToUnderscore(params);
			}
			json = parsed;
		}
		else {
			json = singleValueToUnderscore(params);
		}
		
		try {
			return rawDoFiddle(workspaceId, fiddleId,
					json);
		} finally {
			context.stop();
		}
	}
	
	private Response rawDoFiddle(final WorkspaceId workspaceId,
			final FiddleId fiddleId, final JsonNode json)
			throws JsonProcessingException, IOException {

		final Optional<Response> r = findFiddle(workspaceId, fiddleId)
				.transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(Fiddle fiddle) {
						return executor.execute(
								repository.resources(workspaceId)
										.open(ResourceFileName.VALUE)
										.or(Resources.EMPTY), fiddleId, fiddle,
								json);
					}
				});

		return r.or(Response
				.status(404)
				.entity("no found fiddle in " + workspaceId + " script "
						+ fiddleId).build());
	}

}
