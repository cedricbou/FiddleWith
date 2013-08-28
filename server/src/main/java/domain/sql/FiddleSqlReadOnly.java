package domain.sql;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import domain.exceptions.SqlException;

public class FiddleSqlReadOnly {

	private final DBI jdbi;
	
	private static final Pattern SELECT_QUERY = Pattern.compile("^select\\s+", Pattern.CASE_INSENSITIVE);
	
	public FiddleSqlReadOnly(final DBI jdbi) {
		this.jdbi = jdbi;
	}
	
	public Map<String, Object> singleLine(final String query) {
		if(SELECT_QUERY.matcher(query).find()) {
			final Handle h =jdbi.open();
			
			final List<Map<String, Object>> results = h.select(query);

			h.close();

			if(results.size() > 1) {
				throw SqlException.MULTI_LINE(query, results.size());
			}
			
			return results.get(0);
		}
		else {
			throw SqlException.READ_ONLY(query);
		}
	}
}
