package domain;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;


public class FiddleResponse {

	private final ObjectMapper mapper;
	
	public FiddleResponse(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	public Response ok(final Object entity) {
		return Response.ok(mapper.valueToTree(entity)).build();
	}
	
	public Response _404() {
		return Response.status(404).build();
	}
	
	public Response _404(final String msg) {
		return Response.status(404).tag(msg).build();
	}
}
