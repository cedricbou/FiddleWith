package fiddle.camel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.NoResultsException;

public class HowToUse {

	private final static JdbcConnectionPool pool = JdbcConnectionPool.create(
			"jdbc:h2:mem:test2", "username", "password");

	private static DBI dbi;
	
	@BeforeClass
	public static void initDb() {
		dbi = new DBI(pool);

		try (Connection con = pool.getConnection()) {
			RunScript
					.execute(con, new FileReader("src/test/config/create.sql"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
