package domain;

import com.fasterxml.jackson.databind.JsonNode;

public interface Bidouille {

	public void execute(final JsonNode params);
}
