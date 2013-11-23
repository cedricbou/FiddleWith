package domain.java.serializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.RubyObject;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.builtin.InstanceVariables;
import org.jruby.runtime.builtin.Variable;

@SuppressWarnings("rawtypes")
public class RubyObjectDeserializer implements RubyDeserializer<Map, RubyObject> {

	@Override
	public Map deserialize(RubyObject robj, RubyDeserializerProvider prov) {
		final Map<String, Object> map = new HashMap<String, Object>();

		final InstanceVariables allVars = robj.getInstanceVariables();
		final List<Variable<IRubyObject>> vars = allVars
				.getInstanceVariableList();

		if (robj.hasVariables()) {

			for (final Variable<IRubyObject> var : vars) {
				if (var.getName().startsWith("@")) {
					final IRubyObject o = var.getValue();
					map.put(var.getName().substring(1),
							prov.defaultSerializeValue(o));
				}
			}

		}

		return map;
	}
}
