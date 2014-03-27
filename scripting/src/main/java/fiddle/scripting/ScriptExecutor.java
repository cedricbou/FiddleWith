package fiddle.scripting;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.resources.Resources;

public interface ScriptExecutor {

	public abstract Response executeMethodIn(Resources resources, FiddleId id,
			Fiddle fiddle, String method, JsonNode data);

	public abstract Object rawExecuteMethodIn(Resources resources, FiddleId id, Fiddle fiddle, String method, Object... params);
	
	public abstract Response execute(FiddleId id, Fiddle fiddle, JsonNode data);

	public abstract Response execute(Resources resources, FiddleId id,
			Fiddle fiddle, JsonNode data);

}