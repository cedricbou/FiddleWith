package ressources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

import org.jruby.embed.EvalFailedException;
import org.jruby.exceptions.RaiseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

import domain.ExecutableFiddle;
import domain.FiddleEnvironment;
import domain.User;
import domain.repo.FiddleWorkspace;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;

@Path("/fiddle/with/{workspaceId}/{scriptId}")
public class FiddleResource {

	private final FiddleEnvironment env;

	public FiddleResource(FiddleEnvironment env) {
		this.env = env;
	}

	@Timed
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@Valid @PathParam("scriptId") final FiddleId scriptId,
			final String body) throws JsonProcessingException, IOException {
		return doFiddle(workspaceId, scriptId, body);
	}

	@Timed
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFiddle(
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("scriptId") @Valid final FiddleId scriptId,
			@Context UriInfo uri) throws JsonProcessingException, IOException {
		return doFiddle(workspaceId, scriptId,
				ResourceJsonUtils.autoJson(uri.getQueryParameters()));
	}

	@GET
	@Path("/edit")
	@Produces(MediaType.TEXT_HTML)
	public Response editFiddle(@Auth final User user,
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("scriptId") @Valid final FiddleId scriptId) {

		final Optional<Response> r = findFiddle(workspaceId, scriptId)
				.transform(new Function<ExecutableFiddle, Response>() {
					@Override
					public Response apply(final ExecutableFiddle fiddle) {
						return Response.ok(new ScriptView(scriptId, fiddle))
								.build();
					}
				});

		return r.or(Response.status(404).build());
	}

	@PUT
	@Path("/save")
	@Produces(MediaType.TEXT_HTML)
	public Response saveFiddle(@Auth final User user,
			@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
			@PathParam("scriptId") @Valid final FiddleId scriptId,
			final String content) throws IOException {

		final Optional<Response> r = findFiddle(workspaceId, scriptId)
				.transform(new Function<ExecutableFiddle, Response>() {
					@Override
					public Response apply(final ExecutableFiddle fiddle) {
						try {
							Optional<? extends ExecutableFiddle> modifiedFiddle = replaceFiddle(
									workspaceId, scriptId,
									fiddle.withScript(content));
							if (modifiedFiddle.isPresent()) {
								return Response.ok(
										new ScriptView(scriptId, modifiedFiddle
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


	private Optional<? extends ExecutableFiddle> findFiddle(final WorkspaceId wId,
			final FiddleId fId) {
		final Optional<FiddleWorkspace> wk = env.repository.find(wId);
		if (wk.isPresent()) {
			return wk.get().find(fId);
		}
		return Optional.absent();
	}

	private Optional<? extends ExecutableFiddle> replaceFiddle(WorkspaceId wId,
			final FiddleId fId, ExecutableFiddle fiddle) throws IOException {

		final Optional<FiddleWorkspace> wk = env.repository.find(wId);

		if (wk.isPresent()) {
			return Optional.of(wk.get().replace(fId, fiddle));
		}

		return Optional.absent();
	}

	private Response doFiddle(final WorkspaceId workspaceId,
			final FiddleId fiddleId, final String jsonParams)
			throws JsonProcessingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode json = mapper.readTree(jsonParams);

		final Optional<Response> r = findFiddle(workspaceId,
				fiddleId).transform(new Function<ExecutableFiddle, Response>() {
			@Override
			public Response apply(ExecutableFiddle fiddle) {
				return executeFiddle(fiddle, json);
			}
		});

		return r.or(Response.status(404).entity("no found fiddle in " + workspaceId + " script " + fiddleId).build());
	}

	private Response executeFiddle(final ExecutableFiddle fiddle, final JsonNode json) {
		try {
			return fiddle.execute(json);
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
