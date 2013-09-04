package domain;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import domain.repo.Language;

public interface Fiddle {

	public Response execute(final JsonNode data);
	
	public String getScript();
	
	public Language getLanguage();
	
	public Fiddle withScript(final String content);
}
