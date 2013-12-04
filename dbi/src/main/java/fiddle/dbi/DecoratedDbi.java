package fiddle.dbi;

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
	
	public Map<String, Object> single(final String query, Object... params) {
		try( final DecoratedHandle h = handle() ) {
			return h.single(query, params);
		}
	}
	
	public long number(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.number(query, params);
		}
	}
	
	public String text(final String query, Object... params) {
		try (final DecoratedHandle h = handle() ) {
			return h.text(query, params);
		}
	}

}
