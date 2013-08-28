package ressources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.metrics.annotation.Timed;

import domain.Fiddle;
import domain.FiddleEnvironment;

@Path("/fiddle/with")
@Produces(MediaType.APPLICATION_JSON)
public class FiddleResource {
	
	private final FiddleEnvironment env;
	
	public FiddleResource(FiddleEnvironment env) {
		this.env = env;
	}
	
	@Timed
	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute(@QueryParam("id") final String id, final String body) throws JsonProcessingException, IOException {
		final Fiddle fiddle = env.repository.find(id);

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode json = mapper.readTree(body);

		return fiddle.execute(json);
	}
}
