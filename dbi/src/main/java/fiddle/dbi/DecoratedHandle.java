package fiddle.dbi;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.exceptions.NoResultsException;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class DecoratedHandle implements AutoCloseable {

	private final Handle handle;

	public DecoratedHandle(final Handle handle) {
		this.handle = handle;
	}

	public Map<String, Object> first(final String query, Object... params) {
		return notNullFirst(autoBind(handle.createQuery(query), params).setMaxRows(1));
	}

	public long number(final String query, Object... params) {
		return notNullFirst(autoBind(handle.createQuery(query), params).setMaxRows(1)
				.map(LongMapper.FIRST));

	}

	public List<Long> numbers(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params)
				.map(LongMapper.FIRST).list();
	}

	public String text(final String query, Object... params) {
		return notNullFirst(autoBind(handle.createQuery(query), params).setMaxRows(1)
				.map(StringMapper.FIRST));
	}

	public List<String> texts(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params)
				.map(StringMapper.FIRST).list();
	}
	
	public List<Map<String, Object>> query(final String query, Object... params) {
		return autoBind(handle.createQuery(query), params).list();
	}
	
	public long update(final String update, Object... params) {
		return handle.update(update, params);
	}
	
	public Object inTransaction(final TransactionFunction func) {
		return handle.inTransaction(new TransactionCallback<Object>() {
			@Override
			public Object inTransaction(Handle hdl, TransactionStatus ts)
					throws Exception {
				return func.apply(new DecoratedHandle(hdl));
			}
		});
	}

	@Override
	public void close() {
		handle.close();
	}

	private <T> T notNullFirst(Query<T> q) {
		final T r = q.first();

		if (r == null) {
			throw new NoResultsException("query did not return results (null)",
					q.getContext());
		} else {
			return r;
		}
	}

	private Query<Map<String, Object>> autoBind(
			final Query<Map<String, Object>> query, int pos,
			LinkedList<Object> params) {
		if (params.size() == 0) {
			return query;
		} else {
			return autoBind(query.bind(pos, params.pollFirst()), pos + 1,
					params);
		}
	}

	private Query<Map<String, Object>> autoBind(
			final Query<Map<String, Object>> query, final Object... params) {
		return autoBind(query, 0, Lists.newLinkedList(new Iterable<Object>() {
			@Override
			public Iterator<Object> iterator() {
				return Iterators.forArray(params);
			}
		}));
	}
}
