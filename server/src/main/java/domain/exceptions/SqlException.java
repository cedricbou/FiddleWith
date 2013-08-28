package domain.exceptions;

public class SqlException extends RuntimeException {

	public static SqlException READ_ONLY(final String query) {
		return new SqlException("Read only queries accepted, got query : " + query);
	}

	public static SqlException MULTI_LINE(final String query, final int count) {
		return new SqlException("Queries returns multiple line but single line query was invoked. Found " + count + " results, query : " + query);
	}

	private SqlException(final String msg) {
		super(msg);
	}
	
	public SqlException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
