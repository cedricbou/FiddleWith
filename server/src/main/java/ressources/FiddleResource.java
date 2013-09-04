package ressources;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jruby.embed.EvalFailedException;
import org.jruby.exceptions.RaiseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.metrics.annotation.Timed;

import domain.Fiddle;
import domain.FiddleEnvironment;
import domain.User;

@Path("/fiddle/with/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class FiddleResource {

	private final FiddleEnvironment env;

	public FiddleResource(FiddleEnvironment env) {
		this.env = env;
	}

	@Timed
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postFiddle(@PathParam("id") final String id,
			final String body) throws JsonProcessingException, IOException {
		return doFiddle(id.toString(), body);
	}

	@Timed
	@GET
	public Response getFiddle(@PathParam("id") final String id,
			@Context UriInfo uri) throws JsonProcessingException, IOException {
		return doFiddle(id.toString(), autoJson(uri.getQueryParameters()));
	}

	@GET
	@Path("/edit")
	@Produces(MediaType.TEXT_HTML)
	public Response editFiddle(@Auth final User user,
			@PathParam("id") final String id) {
		return checkForError(id).or(
				Response.ok(new ScriptView(id, findFiddle(id).get())).build());
	}

	@PUT
	@Path("/save")
	@Produces(MediaType.TEXT_HTML)
	public Response saveFiddle(@Auth final User user,
			@PathParam("id") final String id, final String content)
			throws IOException {
		return checkForError(id).or(
				Response.ok(
						new ScriptView(id, env.repository.replace(id,
								findFiddle(id).get().withScript(content))))
						.build());
	}

	private final static Pattern REGEX_FIDDLE_ID = Pattern.compile("^\\w+$");

	/*
	 * Will return a Response if the fiddle does not exist or is forbidden.
	 * (Would have rathered Either like in Scala, but Guava does not have such).
	 */
	private Optional<Response> checkForError(final String fiddleId) {
		if (!validFiddleId(fiddleId)) {
			return Optional.of(Response.status(403)
					.entity("forbidden fiddle '" + fiddleId + "'").build());
		}

		if (!findFiddle(fiddleId).isPresent()) {
			return Optional.of(Response.status(404)
					.entity("no such fiddle '" + fiddleId + "'").build());
		}

		return Optional.absent();
	}

	private Optional<? extends Fiddle> findFiddle(final String fiddleId) {
		if (validFiddleId(fiddleId)) {
			return env.repository.find(fiddleId);
		} else {
			return Optional.absent();
		}
	}

	private boolean validFiddleId(final String fiddleId) {
		return fiddleId != null && REGEX_FIDDLE_ID.matcher(fiddleId).matches();
	}

	private Response doFiddle(final String fiddleId, final String jsonParams)
			throws JsonProcessingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode json = mapper.readTree(jsonParams);

		try {
		return checkForError(fiddleId).or(
				findFiddle(fiddleId).get().execute(json));
		} catch(EvalFailedException e) {
			if(e.getCause() != null && e.getCause() instanceof RaiseException) {
				final StringWriter w = new StringWriter();
				e.getCause().printStackTrace(new PrintWriter(w));
				return Response.status(500).entity(w.toString()).build();
			}
			else {
				return Response.status(500).entity(e.getMessage()).build();
			}
		} catch(Exception e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

	private String autoJson(MultivaluedMap<String, String> map) {
		String json = "{";

		final Set<String> keys = map.keySet();
		int j = 0;

		for (final String key : keys) {
			json += "\"" + key.trim() + "\":";

			final List<String> values = map.get(key);

			if (values.size() > 1) {
				json += "[";

				int i = 0; // Why are there no native map, grep, flat, filter or
							// flatMap in Java :'(

				for (final String value : values) {
					json += asJsonString(value);

					if (++i < values.size()) {
						json += ",";
					}
				}

				json += "]";
			} else {
				json += asJsonString(map.getFirst(key));
			}

			if (++j < keys.size()) {
				json += ",";
			}
		}

		json += "}";

		return json;
	}

	private String asJsonString(final String str) {
		// TODO : check if json with Jackson ?
		// TODO : try to detect numeric and boolean when not json
		final String trimmed = str.trim();
		if ((trimmed.startsWith("[") && trimmed.endsWith("]"))
				|| (trimmed.startsWith("{") && trimmed.endsWith("}"))
				|| (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
			return trimmed;
		} else {
			if (trimmed.matches("\\d+") || trimmed.matches("\\d*\\.\\d+")) {
				return trimmed;
			} else if (trimmed.equals("true") || trimmed.equals("false")) {
				return trimmed;
			} else {
				return "\"" + str + "\"";
			}
		}
	}
}
