package resources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jruby.embed.EvalFailedException;
import org.jruby.exceptions.RaiseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import com.yammer.metrics.core.TimerContext;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.FiddleDBIFactory;
import fiddle.config.Resources;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

@Path("/fiddle/with/{workspaceId}/{fiddleId}")
public class FiddleResource {

	private final FiddleMetrics metrics = new FiddleMetrics();
	
	private final Resources resources;
	private final RubyExecutor executor;
	private final Repository repository;
	private final FiddleDBIFactory dbiFactory;

	public FiddleResource(final Resources resources,
			final Repository repository, final FiddleDBIFactory dbiFactory) {
		this.resources = resources;
		this.executor = new RubyExecutor();
		this.repository = repository;
		this.dbiFactory = dbiFactory;
	}

	@Timed
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@Valid @PathParam("fiddleId") final FiddleId fiddleId,
			final String body) throws JsonProcessingException, IOException {

		final TimerContext context = metrics.timer(workspaceId, fiddleId).time();

		try {
			return doFiddle(workspaceId, fiddleId, body);
		} finally {
			context.stop();
		}
	}

	@Timed
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId,
			@Context UriInfo uri) throws JsonProcessingException, IOException {
		
		final TimerContext context = metrics.timer(workspaceId, fiddleId).time();

		try {
			return doFiddle(workspaceId, fiddleId,
					ResourceJsonUtils.autoJson(uri.getQueryParameters()));
		} finally {
			context.stop();
		}
	}

	@POST
	@Path("/clear")
	public String clearCachePOST(@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId) {
		return clearCache(workspaceId, fiddleId);
	}
	
	@GET
	@Path("/clear")
	public String clearCache(@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId) {
		repository.fiddles(workspaceId).clearCacheFor(fiddleId);
		return "cleared";
	}
	
	private Optional<Fiddle> findFiddle(final WorkspaceId wId,
			final FiddleId fId) {
		return repository.fiddles(wId).open(fId);
	}

	private Response doFiddle(final WorkspaceId workspaceId,
			final FiddleId fiddleId, final String jsonParams)
			throws JsonProcessingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode json = mapper.readTree(jsonParams);

		final Optional<Response> r = findFiddle(workspaceId, fiddleId)
				.transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(Fiddle fiddle) {
						return executeFiddle(workspaceId, fiddleId, fiddle,
								json);
					}
				});

		return r.or(Response
				.status(404)
				.entity("no found fiddle in " + workspaceId + " script "
						+ fiddleId).build());
	}

	private Response executeFiddle(final WorkspaceId wId, final FiddleId id,
			final Fiddle fiddle, final JsonNode json) {
		try {
			return executor.execute(
					repository.resources(resources, dbiFactory, wId), id,
					fiddle, json);
		} catch (EvalFailedException e) {
			if (e.getCause() != null && e.getCause() instanceof RaiseException) {
				final StringWriter w = new StringWriter();
				e.getCause().printStackTrace(new PrintWriter(w));
				return Response.status(500).entity(w.toString()).build();
			} else {
				e.printStackTrace();
				return Response.status(500).entity(e.getMessage()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
}
