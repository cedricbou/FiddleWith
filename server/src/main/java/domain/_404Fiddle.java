package domain;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;


public class _404Fiddle implements Fiddle {
	
	private final String message;
	
	public _404Fiddle(final String message) {
		this.message = message;
	}
	
	@Override
	public Response execute(JsonNode data) {
		return Response.status(404).entity(message).build();
	}
	
	@Override
	public String getScript() {
		return "response._404('" + message + "')";
	}
}
