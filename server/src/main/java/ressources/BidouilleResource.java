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

import domain.Bidouille;
import domain.BidouilleRepository;

@Path("/bidouille")
@Produces(MediaType.APPLICATION_JSON)
public class BidouilleResource {

	private final BidouilleRepository repo;
	
	public BidouilleResource(final BidouilleRepository repo) {
		this.repo = repo;
	}
	
	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute(@QueryParam("id") final String id, final String body) throws JsonProcessingException, IOException {
		final Bidouille bidouille = repo.find(id);

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode data = mapper.readTree(body);

		data.get(index)
		
		bidouille.execute(data);
		
		return null;
	}
}
