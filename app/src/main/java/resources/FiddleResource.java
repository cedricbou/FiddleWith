package resources;

import java.io.IOException;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
	
	@GET
	@Path("/edit")
	@Produces(MediaType.TEXT_HTML)
	public Response editFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId) {

		// Let's work with a clean copy of the fiddle.
		clearCache(workspaceId, fiddleId);
		
		final Optional<Fiddle> fiddle = repository.fiddles(workspaceId).open(fiddleId);
		
		
		final Optional<Response> r = fiddle
				.transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(final Fiddle fiddle) {
						return Response.ok(new ScriptView(workspaceId, fiddleId, fiddle))
								.build();
					}
				});

		return r.or(Response.status(404).build());
	}
	
	@PUT
	@Path("/save")
	@Produces(MediaType.TEXT_HTML)
	public Response saveFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId,
			final String content) throws IOException {

		final Optional<Response> r = repository.fiddles(workspaceId).open(fiddleId)
				.transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(final Fiddle fiddle) {
						try {
							repository.fiddles(workspaceId).write(fiddleId, new Fiddle(content, fiddle.language));
							repository.fiddles(workspaceId).clearCacheFor(fiddleId);
							final Optional<Fiddle> modifiedFiddle = repository.fiddles(workspaceId).open(fiddleId);
							
							if (modifiedFiddle.isPresent()) {
								return Response.ok(
										new ScriptView(workspaceId, fiddleId, modifiedFiddle
												.get())).build();
							}
							
							return Response.status(500)
									.entity("tried to modify absent fiddle!")
									.build();
						} catch (IOException ioe) {
							return Response.status(500)
									.entity("failed to write fiddle!").build();
						}
					}
				});

		return r.or(Response.status(404).build());
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
						return executor.execute(
								repository.resources(resources, dbiFactory, workspaceId), fiddleId,
								fiddle, json);
					}
				});

		return r.or(Response
				.status(404)
				.entity("no found fiddle in " + workspaceId + " script "
						+ fiddleId).build());
	}

}
