package fiddle.scripting;

import java.util.List;

import fiddle.api.Fiddle;

public interface ScriptMethodMatcher {

	public abstract List<String> methodsFor(Fiddle fiddle);

}