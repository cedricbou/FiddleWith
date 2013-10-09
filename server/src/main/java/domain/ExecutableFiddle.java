package domain;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import fiddle.api.Language;

public interface ExecutableFiddle {

	public Response execute(final JsonNode data);
	
	public String getScript();
	
	public Language getLanguage();
	
	public ExecutableFiddle withScript(final String content);
}
