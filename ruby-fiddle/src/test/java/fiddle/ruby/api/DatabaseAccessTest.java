package fiddle.ruby.api;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.h2.jdbcx.JdbcConnectionPool;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.Resources;
import fiddle.dbi.DecoratedDbi;
import fiddle.dbi.registry.DbiRegistry;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

public class DatabaseAccessTest {

	private final Repository repo;

	private final static WorkspaceId WS = new WorkspaceId("db");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	private final Resources resources = mock(Resources.class);

	private final DbiRegistry dbis = mock(DbiRegistry.class);

	private final DBI dbi = new DBI(JdbcConnectionPool.create(
			"jdbc:h2:mem:test2", "username", "password"));

	public DatabaseAccessTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		when(dbis.contains("foo")).thenReturn(true);
		when(dbis.get("foo")).thenReturn(dbi);
		when(dbis.getDecoratedDbi("foo")).thenReturn(new DecoratedDbi(dbi));
		when(resources.dbis()).thenReturn(dbis);

		repo = new Repository(repoDir);
	}

	@Test
	public void testConcatTwoSelects() throws JsonProcessingException,
			IOException {
		final FiddleId id = new FiddleId("select1");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());

		final String expected = "Greetings! on "
				+ LocalDate.now().toString("YYYY-MM-dd");
		
		System.out.println("[" + r.getEntity().toString() + "]");
		
		assertTrue(
				"a fiddle will return the concatenation of a greetings string and a date : " + expected,
				r.getEntity().toString().startsWith(expected));

	}

	public void testReadSimpleData() throws JsonProcessingException,
			IOException {
		final FiddleId id = new FiddleId("read");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{\"name\" : \"doe\"}"));

		assertEquals(200, r.getStatus());

		assertThat(
				"a fiddle will return a serialized person",
				MAPPER.writeValueAsString(r.getEntity()),
				is(equalTo(jsonFixture("fiddle/ruby/api/fixtures/person.json"))));

	}

}
