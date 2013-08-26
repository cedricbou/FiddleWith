package domain.json.serializer;

import java.io.IOException;

import org.jruby.RubyString;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyStringSerializer extends JsonSerializer<RubyString> {
	
	@Override
	public void serialize(RubyString rstr, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {
		
		gen.writeString(rstr.decodeString());
	}
		

}
