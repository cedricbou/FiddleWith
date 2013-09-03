package domain;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

public interface Fiddle {

	public Response execute(final JsonNode data);
	
	public String getScript();
}
