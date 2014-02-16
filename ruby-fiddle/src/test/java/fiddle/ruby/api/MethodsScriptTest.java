package fiddle.ruby.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.manager.RepositoryManager;
import fiddle.resources.Resources;
import fiddle.ruby.RubyExecutor;

public class MethodsScriptTest {

	private final RepositoryManager repo;

	private final static WorkspaceId WS = new WorkspaceId("methods");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	public MethodsScriptTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		repo = new RepositoryManager(repoDir, null);
	}

	@Test
	public void callHealthcheckTest() throws JsonProcessingException, IOException {
		final FiddleId add = new FiddleId("healthcheck");
		final Optional<Fiddle> f = repo.fiddles(WS).open(add);

		final Response r = ex.executeMethodIn(Resources.EMPTY, add, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals("health check ran!", r.getEntity().toString());
	}

}
