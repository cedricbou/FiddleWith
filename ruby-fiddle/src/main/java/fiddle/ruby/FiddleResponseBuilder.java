package fiddle.ruby;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fiddle.httpclient.FiddleHttpResponse;


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
		} else if(normalized.isTextual()) {
			return Response.ok(normalized.asText()).build();
		}
		
		return Response.ok(normalized).build();
	}
	
	public Response auto(final FiddleHttpResponse r) {
		if(r.is2XX()) {
			return Response.ok(r.body()).build();
		}
		else {
			return Response.status(r.status()).entity(r.statusReason()).build();
		}
	}
	
	public Response _404() {
		return Response.status(404).build();
	}
	
	public Response _404(final String msg) {
		return Response.status(404).tag(msg).build();
	}
}
