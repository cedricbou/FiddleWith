package ressources;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.metrics.annotation.Timed;

import domain.Fiddle;
import domain.FiddleEnvironment;
import domain._404Fiddle;

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
	public ScriptView editFiddle(@PathParam("id") final String id) {
		return new ScriptView(findFiddle(id).getScript());
	}
	
	private final static Pattern REGEX_FIDDLE_ID = Pattern.compile("^\\w+$");

	private Fiddle findFiddle(final String fiddleId) {
		if (fiddleId != null && REGEX_FIDDLE_ID.matcher(fiddleId).matches()) {
			return env.repository.find(fiddleId);
		}
		else {
			return new _404Fiddle("forbidden fiddle id '" + fiddleId + "'");
		}
	}
	
	private Response doFiddle(final String fiddleId, final String jsonParams)
			throws JsonProcessingException, IOException {

			final Fiddle fiddle = findFiddle(fiddleId);

			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode json = mapper.readTree(jsonParams);

			return fiddle.execute(json);
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
