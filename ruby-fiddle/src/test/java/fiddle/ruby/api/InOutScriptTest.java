package fiddle.ruby.api;

import static com.yammer.dropwizard.testing.JsonHelpers.jsonFixture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.impl.RepositoryManager;
import fiddle.ruby.RubyExecutor;

public class InOutScriptTest {

	private final RepositoryManager repo;

	private final static WorkspaceId WS_SIMPLE = new WorkspaceId("inout");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	public InOutScriptTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		repo = new RepositoryManager(repoDir, null);
	}

	@Test
	public void additionTest() throws JsonProcessingException, IOException {
		final FiddleId add = new FiddleId("addition");
		final Optional<Fiddle> f = repo.fiddles(WS_SIMPLE).open(add);

		final Response r = ex.execute(add, f.get(),
				MAPPER.readTree("{\"a\" : 14, \"b\" : 16}"));

		assertEquals(200, r.getStatus());
		assertEquals(30L, r.getEntity());
	}

	@Test
	public void severalAdditionsTest() throws JsonProcessingException,
			IOException {
		final FiddleId add = new FiddleId("addition");
		final Optional<Fiddle> f = repo.fiddles(WS_SIMPLE).open(add);

		final JsonNode[] nodes = new JsonNode[] {
				MAPPER.readTree("{\"a\" : 14, \"b\" : 16}"),
				MAPPER.readTree("{\"a\" : 7, \"b\" : 23}"),
				MAPPER.readTree("{\"a\" : 89, \"b\" : -59}"),
				MAPPER.readTree("{\"a\" : 30, \"b\" : 0}"), };

		for (final JsonNode node : nodes) {
			final Response r = ex.execute(add, f.get(), node);
			assertEquals(200, r.getStatus());
			assertEquals(30L, r.getEntity());
		}
	}

	@Test
	public void hashTest() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("hash");
		final Optional<Fiddle> f = repo.fiddles(WS_SIMPLE).open(id);

		final JsonNode node = MAPPER
				.readTree("{\"person\" : {\"name\" : \"john doe\", \"phone\" : \"+333200000\", \"age\" : 25}}");

		final Response r = ex.execute(id, f.get(), node);
		assertEquals(200, r.getStatus());

		assertThat("a fiddle will return a serialized person",
				MAPPER.writeValueAsString(r.getEntity()),
				is(equalTo(jsonFixture("fiddle/ruby/api/fixtures/person.json"))));
	}

	@Test
	public void arrayTest() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("array");
		final Optional<Fiddle> f = repo.fiddles(WS_SIMPLE).open(id);

		final JsonNode node = MAPPER
				.readTree("{\"arr\" : [1, 2, 3, 4, 5]}");

		final Response r = ex.execute(id, f.get(), node);
		assertEquals(200, r.getStatus());

		assertThat("a fiddle will return a reversed array",
				MAPPER.writeValueAsString(r.getEntity()),
				is(equalTo(jsonFixture("fiddle/ruby/api/fixtures/1to5.json"))));
	}

}
