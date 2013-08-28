package domain.ruby.sql;

import java.util.Map;
import java.util.Map.Entry;

import org.jruby.Ruby;
import org.jruby.RubyHash;
import org.jruby.RubyNil;

import domain.sql.FiddleSqlReadOnly;

public class JRubySqlReadOnlyAdapter {

	private final FiddleSqlReadOnly sqlRO;

	private final Ruby runtime;

	public JRubySqlReadOnlyAdapter(FiddleSqlReadOnly sqlRO, Ruby runtime) {
		this.sqlRO = sqlRO;
		this.runtime = runtime;
	}

	public RubyHash singleLine(String query) {
		final Map<String, Object> map = sqlRO.singleLine(query);
		final RubyHash h = new RubyHash(runtime, new RubyNil(runtime));
		for (final Entry e : map.entrySet()) {
			h.put(e.getKey(), e.getValue());
		}
		return h;
	}
}
