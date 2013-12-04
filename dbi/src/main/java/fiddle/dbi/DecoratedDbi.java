package fiddle.dbi;

import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.DBI;

public class DecoratedDbi {

	private final DBI dbi;

	public DecoratedDbi(final DBI dbi) {
		this.dbi = dbi;
	}

	public DecoratedHandle handle() {
		return new DecoratedHandle(dbi.open());
	}
	
	public Map<String, Object> first(final String query, Object... params) {
		try( final DecoratedHandle h = handle() ) {
			return h.first(query, params);
		}
	}
	
	public long number(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.number(query, params);
		}
	}
	
	public List<Long> numbers(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.numbers(query, params);
		}
	}
	
	public String text(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.text(query, params);
		}
	}

	public List<String> texts(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.texts(query, params);
		}
	}

	public List<Map<String, Object>> query(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.query(query, params);
		}
	}
	
	public long update(final String update, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.update(update, params);
		}
	}
	
	public Object inTransaction(final TransactionFunction func) {
		try (final DecoratedHandle h = handle() ) {
			return h.inTransaction(func);
		}
	}

}
