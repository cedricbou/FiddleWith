package domain;

import com.fasterxml.jackson.databind.JsonNode;

public interface Fiddle {

	public void execute(final JsonNode params);
}
