package fiddle.dbi;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class DecoratedHandle implements AutoCloseable {

	private final Handle handle;
	
	public DecoratedHandle(final Handle handle) {
		this.handle = handle;
	}
	
	public Map<String, Object> single(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params).setMaxRows(1).first();
	}
	
	public long number(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params).setMaxRows(1).map(LongMapper.FIRST).first();
	}
	
	public String text(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params).setMaxRows(1).map(StringMapper.FIRST).first();
	}
	
	@Override
	public void close() {
		handle.close();
	}

	private Query<Map<String, Object>> autoBind(final Query<Map<String, Object>> query, int pos, LinkedList<Object> params) {
		if(params.size() == 0) {
			return query;
		}
		else {
			return autoBind(query.bind(pos, params.pollFirst()), pos + 1, params);
		}
	}

	private Query<Map<String, Object>> autoBind(final Query<Map<String, Object>> query, final Object... params) {
		return autoBind(query, 1, Lists.newLinkedList(new Iterable<Object>() {
			@Override
			public Iterator<Object> iterator() {
				return Iterators.forArray(params);
			}
		}));
	}
}
