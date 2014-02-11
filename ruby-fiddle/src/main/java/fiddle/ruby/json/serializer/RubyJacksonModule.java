package fiddle.ruby.json.serializer;

import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.java.proxies.JavaProxy;

import com.fasterxml.jackson.databind.module.SimpleModule;


public final class RubyJacksonModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	public final static RubyJacksonModule MODULE = new RubyJacksonModule();
	
	public RubyJacksonModule() {
    	addSerializer(RubyNumeric.class, new RubyNumericSerializer());
    	addSerializer(RubyString.class, new RubyStringSerializer());
    	addSerializer(RubyBoolean.class, new RubyBooleanSerializer());
    	addSerializer(RubyArray.class, new RubyArraySerializer());
    	addSerializer(RubyHash.class, new RubyHashSerializer());
    	addSerializer(RubyObject.class, new RubyObjectSerializer());
    	addSerializer(JavaProxy.class, new JavaProxySerializer());
    }
}