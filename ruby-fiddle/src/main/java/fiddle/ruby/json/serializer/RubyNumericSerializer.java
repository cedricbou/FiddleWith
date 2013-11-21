package fiddle.ruby.json.serializer;

import java.io.IOException;

import org.jruby.RubyBignum;
import org.jruby.RubyFloat;
import org.jruby.RubyInteger;
import org.jruby.RubyNumeric;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class RubyNumericSerializer extends JsonSerializer<RubyNumeric> {
	
	@Override
	public void serialize(RubyNumeric rnum, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {
		
		if(rnum instanceof RubyFloat) {
			gen.writeNumber(rnum.getDoubleValue());
		}
		else if(rnum instanceof RubyInteger) {
			gen.writeNumber(rnum.getLongValue());
		}
		else if(rnum instanceof RubyBignum) {
			gen.writeNumber(rnum.getBigIntegerValue());
		}
		else {
			gen.writeString(rnum.toString());
		}
	}
}