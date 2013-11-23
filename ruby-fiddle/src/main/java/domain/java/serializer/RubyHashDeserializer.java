package domain.java.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jruby.RubyHash;

@SuppressWarnings("rawtypes")
public class RubyHashDeserializer implements RubyDeserializer<Map, RubyHash> {

	@Override
	public Map deserialize(RubyHash rh, RubyDeserializerProvider prov) {
		final Map<String, Object> map = new HashMap<String, Object>();
		
		@SuppressWarnings("unchecked")
		final Set<Entry> set = rh.directEntrySet();
		
		for (final Entry entry : set) {
			map.put(entry.getKey().toString(), prov.defaultSerializeValue(entry.getValue()));
		}

		return map;
	}

}
