package domain.java.serializer;

import org.jruby.RubyBignum;
import org.jruby.RubyFloat;
import org.jruby.RubyInteger;
import org.jruby.RubyNumeric;

public class RubyNumericDeserializer implements RubyDeserializer<Number, RubyNumeric> {
	
	@Override
	public Number deserialize(RubyNumeric rnum, RubyDeserializerProvider prov) {
		if(rnum instanceof RubyFloat) {
			return rnum.getDoubleValue();
		}
		else if(rnum instanceof RubyInteger) {
			return rnum.getLongValue();
		}
		else if(rnum instanceof RubyBignum) {
			return rnum.getBigIntegerValue();
		}
		else {
			return Double.parseDouble(rnum.toString());
		}
	}
}