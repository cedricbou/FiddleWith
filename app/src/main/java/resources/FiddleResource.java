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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.execution.FiddleExecutionService;
import fiddle.password.PasswordManager.UserConfiguration;
import fiddle.repository.CacheableRepository;
import fiddle.repository.manager.RepositoryManager;
import fiddle.scripting.ScriptExecutor;

@Path("/fiddle/with/{workspaceId}/{fiddleId}")
public class FiddleResource {

	private final RepositoryManager repository;
	private final FiddleExecutionService fiddleRun;

	public FiddleResource(final RepositoryManager repository, final ScriptExecutor executor) {
		this.fiddleRun = new FiddleExecutionService(repository, executor);
		this.repository = repository;
	}

	@Timed
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@Valid @PathParam("fiddleId") final FiddleId fiddleId,
			final String body) throws JsonProcessingException, IOException {

		return fiddleRun.doFiddle(workspaceId, fiddleId, body);
	}

	@Timed
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId,
			@Context UriInfo uri) throws JsonProcessingException, IOException {

		return fiddleRun.doFiddle(workspaceId, fiddleId,
					ResourceJsonUtils.autoJson(uri.getQueryParameters()));
	}

	@POST
	@Path("/clear")
	public String clearCachePOST(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId) {
		return clearCache(workspaceId, fiddleId);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/clear")
	public String clearCache(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId) {

		if (repository.fiddles(workspaceId) instanceof CacheableRepository) {
			((CacheableRepository<FiddleId>) repository.fiddles(workspaceId))
					.clearCacheFor(fiddleId);
			return "cleared";
		} else {
			return "not cleared, no cacheable repository";
		}

	}

	@GET
	@Path("/edit")
	@Produces(MediaType.TEXT_HTML)
	public Response editFiddle(@Auth final UserConfiguration user,
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId)
			throws IllegalAccessException {

		user.assertTag("fiddler");

		// Let's work with a clean copy of the fiddle.
		clearCache(workspaceId, fiddleId);

		final Optional<Fiddle> fiddle = repository.fiddles(workspaceId).open(
				fiddleId);

		final Optional<Response> r = fiddle
				.transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(final Fiddle fiddle) {
						return Response.ok(
								new ScriptView(workspaceId, fiddleId, fiddle))
								.build();
					}
				});

		return r.or(Response.status(404).build());
	}

	@PUT
	@Path("/save")
	@Produces(MediaType.TEXT_HTML)
	public Response saveFiddle(@Auth final UserConfiguration user,
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("fiddleId") @Valid final FiddleId fiddleId,
			final String content) throws IOException, IllegalAccessException {

		user.assertTag("fiddler");

		final Optional<Response> r = repository.fiddles(workspaceId)
				.open(fiddleId).transform(new Function<Fiddle, Response>() {
					@Override
					public Response apply(final Fiddle fiddle) {
						try {
							repository.fiddles(workspaceId).write(fiddleId,
									new Fiddle(content, fiddle.language));
							final Optional<Fiddle> modifiedFiddle = repository
									.fiddles(workspaceId).open(fiddleId);

							if (modifiedFiddle.isPresent()) {
								return Response.ok(
										new ScriptView(workspaceId, fiddleId,
												modifiedFiddle.get())).build();
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
}
