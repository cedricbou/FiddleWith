package fiddle.ruby.json.serializer;

import java.io.IOException;
import java.util.List;

import org.jruby.RubyObject;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.builtin.InstanceVariables;
import org.jruby.runtime.builtin.Variable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyObjectSerializer extends JsonSerializer<RubyObject> {

	@Override
	public void serialize(RubyObject robj, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {

		final InstanceVariables allVars = robj.getInstanceVariables();
		final List<Variable<IRubyObject>> vars = allVars
				.getInstanceVariableList();

		if(robj.hasVariables()) {
		
		gen.writeStartObject();
		
		for (final Variable<IRubyObject> var : vars) {
			if (var.getName().startsWith("@")) {
				gen.writeFieldName(var.getName().substring(1));

				final IRubyObject o = var.getValue();

				prov.defaultSerializeValue(o, gen);
			}
		}

		gen.writeEndObject();
		
		}
		else {
			gen.writeString(robj.toString());
		}
	}

}
