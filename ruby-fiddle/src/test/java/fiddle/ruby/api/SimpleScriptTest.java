package fiddle.ruby.api;

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
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

public class SimpleScriptTest {

	private final Repository repo;

	private final static WorkspaceId WS_SIMPLE = new WorkspaceId("simple");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	public SimpleScriptTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		repo = new Repository(repoDir);
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

}
