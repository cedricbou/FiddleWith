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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

public class DatabaseAccessTest {

	private final Repository repo;

	private final static WorkspaceId WS = new WorkspaceId("db");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	public DatabaseAccessTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		repo = new Repository(repoDir);
	}

	public void testReadSimpleData() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("read");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final Response r = ex.execute(id, f.get(),
				MAPPER.readTree("{\"name\" : \"doe\"}"));

		assertEquals(200, r.getStatus());

		assertThat("a fiddle will return a serialized person",
				MAPPER.writeValueAsString(r.getEntity()),
				is(equalTo(jsonFixture("fiddle/ruby/api/fixtures/person.json"))));


	}

}
