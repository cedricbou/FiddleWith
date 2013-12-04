package fiddle.dbi;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class HowToUse {

	private final DbiRegistry registry;
	
	public HowToUse() {
		this.registry = new DbiRegistry();
		registry.declare("test", new DBI(JdbcConnectionPool.create("jdbc:h2:mem:test",
                "username",
                "password")));
	}
	
	@Test
	public void testSimpleSelect() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");
		assertEquals(5, db.number("select 5"));
		assertEquals("hello", db.text("select 'hello'"));
		final Map<String, Object> single = db.single("select 34 as n, 'hello' as t");
		assertEquals(34, single.get("n"));
		assertEquals("hello", single.get("t"));
	}

}
