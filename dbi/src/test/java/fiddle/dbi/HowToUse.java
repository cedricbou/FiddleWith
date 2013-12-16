package fiddle.dbi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.NoResultsException;

import fiddle.dbi.registry.SimpleDbiRegistry;

public class HowToUse {

	private final static JdbcConnectionPool pool = JdbcConnectionPool.create(
			"jdbc:h2:mem:test2", "username", "password");

	private final static SimpleDbiRegistry registry = new SimpleDbiRegistry();

	@BeforeClass
	public static void initDb() {
		registry.declare("test", new DBI(pool));

		try (Connection con = pool.getConnection()) {
			RunScript
					.execute(con, new FileReader("src/test/config/create.sql"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testSimpleSelect() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");
		assertEquals(5, db.number("select 5"));
		assertEquals("hello", db.text("select 'hello'"));
		final Map<String, Object> single = db
				.first("select 34 as n, 'hello' as t");
		assertEquals(34, single.get("n"));
		assertEquals("hello", single.get("t"));
	}

	@Test
	public void testQuery() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");

		final List<Map<String, Object>> r = db.query("select * from person where name in (?, ?) order by id", "Dupont", "Doe");
		
		assertEquals(2, r.size());
		assertEquals("Doe", r.get(0).get("name"));
		assertEquals("Dupont", r.get(1).get("name"));	
	}

	@Test
	public void testConstrainedSelect() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");

		try {
			db.number("select id from person where name = ?", "Nobody");
			fail("no id for M. Nobody, it should have failed on this request");
		} catch (NoResultsException e) {
			assertTrue(true);
		}

		try {
			db.text("select name from person where name = ?", "Nobody");
			fail("no name for M. Nobody, it should have failed on this request");
		} catch (NoResultsException e) {
			assertTrue(true);
		}

		try {
			db.first("select * from person where name = ?", "Nobody");
			fail("no data for M. Nobody, it should have failed on this request");
		} catch (NoResultsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSimpleInsert() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");

		final long rows = db.update("insert into person values (?, ?, ?)", 99,
				"Junit", 3);

		final String name = db.text("select name from person where id = ?", 99);
		final long age = db.number("select age from person where id = ?", 99);

		assertEquals(1, rows);
		assertEquals("Junit", name);
		assertEquals(3, age);
	}

	@Test
	public void testSuccessfulTransaction() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");

		db.inTransaction(new TransactionFunction() {

			@Override
			public Object apply(DecoratedHandle handle) {
				final String dad = handle.text(
						"select name from person where id = ?", 1);
				handle.update("insert into person values (?, ?, ?)", 100, dad
						+ " Junior", 1);
				final String son = handle.text(
						"select name from person where id = ?", 100);
				handle.update("insert into birth values (?, ?, now(), ?)", 10,
						son, 100);
				return 100;
			}
		});

		final Map<String, Object> rp = db.first(
				"select * from person where id = ?", 100);
		assertEquals("Doe Junior", rp.get("name"));
		assertEquals(100, rp.get("id"));

		final Map<String, Object> rb = db
				.first("select * from birth where id = 10");
		assertEquals(10, rb.get("id"));
		assertEquals(100, rb.get("person_id"));

	}

	@Test
	public void testRollbackedTransaction() {
		final DecoratedDbi db = registry.getDecoratedDbi("test");

		try {
			db.inTransaction(new TransactionFunction() {

				@Override
				public Object apply(DecoratedHandle handle) {
					final String dad = handle.text(
							"select name from person where id = ?", 2);
					handle.update("insert into person values (?, ?, ?)", 101,
							dad + " Junior", 1);
					final String son = handle.text(
							"select name from person where id = ?", 999); // Force
																			// failure
																			// here,
																			// to
																			// trigger
																			// rollback.
					handle.update("insert into birth values (?, ?, now(), ?)",
							11, son, 101);
					return 101;
				}
			});

			fail("transaction should have failed on exception to trigger rollback");
		} catch (NoResultsException nre) {
			assertTrue(true);
		}

		try {
			db.first("select * from person where id = ?", 101);
			fail("no person should be found, it should have been rolled back");
		} catch (NoResultsException nre) {
			assertTrue(true);
		}

		try {
			db.first("select * from birth where id = 11");
			fail("no birth should be found, it should have been rolled back");
		} catch (NoResultsException nre) {
			assertTrue(true);
		}

	}
}
