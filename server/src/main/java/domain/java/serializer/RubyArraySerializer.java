package domain.java.serializer;

import java.util.LinkedList;
import java.util.List;

import org.jruby.RubyArray;

@SuppressWarnings("rawtypes")
public class RubyArraySerializer
	 implements JavaSerializer<List, RubyArray> {
			
			@SuppressWarnings("unchecked")
			@Override
			public List serialize(RubyArray rarr, JavaSerializerProvider prov) {
				
				final List list = new LinkedList();
				
				final int l = rarr.getLength();
				for(int i = 0; i < l; ++i) {
					list.add(prov.defaultSerializeValue(rarr.get(i)));
				}
				
				return list;
			}
}
