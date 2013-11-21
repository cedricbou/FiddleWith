package fiddle.ruby.json.serializer;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.jruby.RubyHash;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyHashSerializer extends JsonSerializer<RubyHash> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void serialize(RubyHash rh, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {
		
		gen.writeStartObject();
		
		final Set<Entry> set = rh.directEntrySet();
		
		for (final Entry entry : set) {
			gen.writeFieldName(entry.getKey().toString());
			prov.defaultSerializeValue(entry.getValue(), gen);
		}

		gen.writeEndObject();
	}

}
