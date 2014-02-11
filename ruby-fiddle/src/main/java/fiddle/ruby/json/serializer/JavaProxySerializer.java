package fiddle.ruby.json.serializer;

import java.io.IOException;

import org.jruby.java.proxies.JavaProxy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JavaProxySerializer extends JsonSerializer<JavaProxy> {
	
	@Override
	public void serialize(JavaProxy proxy, JsonGenerator gen,
			SerializerProvider prov) throws IOException,
			JsonProcessingException {
			
			prov.defaultSerializeValue(proxy.getObject(), gen);
	}
}
