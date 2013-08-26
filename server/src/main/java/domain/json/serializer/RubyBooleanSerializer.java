package domain.json.serializer;

import java.io.IOException;

import org.jruby.RubyBoolean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyBooleanSerializer extends JsonSerializer<RubyBoolean> {
	
	@Override
	public void serialize(RubyBoolean rbool, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {
		
		gen.writeBoolean(rbool.isTrue());
	}
}