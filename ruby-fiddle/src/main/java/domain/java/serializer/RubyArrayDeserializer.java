package domain.java.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jruby.RubyArray;

@SuppressWarnings("rawtypes")
public class RubyArrayDeserializer
	 implements RubyDeserializer<List, RubyArray> {
			
			@SuppressWarnings("unchecked")
			@Override
			public List deserialize(RubyArray rarr, RubyDeserializerProvider prov) {
				
				final List list = new LinkedList();
				
				final int l = rarr.getLength();
				for(int i = 0; i < l; ++i) {
					list.add(prov.defaultSerializeValue(rarr.get(i)));
				}
				
				return list;
			}
}
