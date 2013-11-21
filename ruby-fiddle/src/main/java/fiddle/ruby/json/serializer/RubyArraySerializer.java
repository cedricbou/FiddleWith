package fiddle.ruby.json.serializer;

import java.io.IOException;

import org.jruby.RubyArray;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyArraySerializer
	 extends JsonSerializer<RubyArray> {
			
			@Override
			public void serialize(RubyArray rarr, JsonGenerator gen,
					SerializerProvider prov) throws IOException,
					JsonProcessingException {
				
				gen.writeStartArray();
			
				final int l = rarr.getLength();
				for(int i = 0; i < l; ++i) {
					prov.defaultSerializeValue(rarr.get(i), gen);
				}
				
				gen.writeEndArray();
			}
}
