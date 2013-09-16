package domain.java.serializer;

import org.jruby.RubyBoolean;

public class RubyBooleanSerializer implements JavaSerializer<Boolean, RubyBoolean> {
	
	@Override
	public Boolean serialize(RubyBoolean rbool, JavaSerializerProvider prov) {
		return rbool.isTrue();
	}
}