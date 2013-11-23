package fiddle.api;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FiddleResponseBuilder {

	private final ObjectMapper mapper;
	
	private final static Logger LOG = LoggerFactory.getLogger(FiddleResponseBuilder.class);
	
	public FiddleResponseBuilder(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	public Response ok(final Object entity) {
		LOG.debug("building ok response for {}", entity);
		
		final JsonNode normalized = mapper.valueToTree(entity);
		
		if(normalized.isLong()) {
			return Response.ok(normalized.asLong()).build();
		}
		
		return Response.ok(normalized).build();
	}
	
	public Response _404() {
		return Response.status(404).build();
	}
	
	public Response _404(final String msg) {
		return Response.status(404).tag(msg).build();
	}
}
