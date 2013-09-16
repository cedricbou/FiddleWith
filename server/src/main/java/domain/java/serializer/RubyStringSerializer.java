package domain.java.serializer;

import org.jruby.RubyString;

public class RubyStringSerializer implements JavaSerializer<String, RubyString> {
	
	@Override
	public String serialize(RubyString rstr, JavaSerializerProvider prov) {
		return rstr.decodeString();
	}
	
}
