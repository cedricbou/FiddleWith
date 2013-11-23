package domain.java.serializer;

import org.jruby.RubyString;

public class RubyStringDeserializer implements RubyDeserializer<String, RubyString> {
	
	@Override
	public String deserialize(RubyString rstr, RubyDeserializerProvider prov) {
		return rstr.decodeString();
	}
	
}
