package domain.java.serializer;

import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;

public class RubyDeserializerProvider {

	private final static RubyHashDeserializer RHASH = new RubyHashDeserializer();
	private final static RubyArrayDeserializer RARR = new RubyArrayDeserializer();
	private final static RubyNumericDeserializer RNUM = new RubyNumericDeserializer();
	private final static RubyStringDeserializer RSTR = new RubyStringDeserializer();
	private final static RubyBooleanDeserializer RBOOL = new RubyBooleanDeserializer();
	private final static RubyObjectDeserializer ROBJ = new RubyObjectDeserializer();
	
	public Object defaultSerializeValue(final Object entity) {
		if(entity instanceof RubyHash) {
			return RHASH.deserialize((RubyHash)entity, this);
		}
		else if(entity instanceof RubyArray) {
			return RARR.deserialize((RubyArray)entity, this);
		}
		else if(entity instanceof RubyObject) {
			return ROBJ.deserialize((RubyObject)entity, this);
		}
		else if(entity instanceof RubyNumeric) {
			return RNUM.deserialize((RubyNumeric)entity, this);
		}
		else if(entity instanceof RubyBoolean) {
			return RBOOL.deserialize((RubyBoolean)entity, this);
		}
		else if(entity instanceof RubyString) {
			return RSTR.deserialize((RubyString)entity, this);
		}
		
		return null;
	}
}
