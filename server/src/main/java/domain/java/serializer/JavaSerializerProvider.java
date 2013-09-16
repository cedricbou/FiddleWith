package domain.java.serializer;

import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;

public class JavaSerializerProvider {

	private final static RubyHashSerializer RHASH = new RubyHashSerializer();
	private final static RubyArraySerializer RARR = new RubyArraySerializer();
	private final static RubyNumericSerializer RNUM = new RubyNumericSerializer();
	private final static RubyStringSerializer RSTR = new RubyStringSerializer();
	private final static RubyBooleanSerializer RBOOL = new RubyBooleanSerializer();
	private final static RubyObjectSerializer ROBJ = new RubyObjectSerializer();
	
	public Object defaultSerializeValue(final Object entity) {
		if(entity instanceof RubyHash) {
			return RHASH.serialize((RubyHash)entity, this);
		}
		else if(entity instanceof RubyArray) {
			return RARR.serialize((RubyArray)entity, this);
		}
		else if(entity instanceof RubyObject) {
			return ROBJ.serialize((RubyObject)entity, this);
		}
		else if(entity instanceof RubyNumeric) {
			return RNUM.serialize((RubyNumeric)entity, this);
		}
		else if(entity instanceof RubyBoolean) {
			return RBOOL.serialize((RubyBoolean)entity, this);
		}
		else if(entity instanceof RubyString) {
			return RSTR.serialize((RubyString)entity, this);
		}
		
		return null;
	}
}
