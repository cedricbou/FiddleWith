package domain.java.serializer;

import org.jruby.RubyBoolean;

public class RubyBooleanDeserializer implements RubyDeserializer<Boolean, RubyBoolean> {
	
	@Override
	public Boolean deserialize(RubyBoolean rbool, RubyDeserializerProvider prov) {
		return rbool.isTrue();
	}
}