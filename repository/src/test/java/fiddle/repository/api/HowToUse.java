package fiddle.repository.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.FiddleRepository;

public class HowToUse {

	private final FiddleRepository repo;

	public HowToUse() {
		URL url = this.getClass().getResource("localised.donoterase");
		File repoDir = new File(new File(url.getFile()).getParentFile(), "repository");
		repo = new FiddleRepository(repoDir);
	}

	@Test
	public void findAFiddleInItsWorkspace() {
		final Optional<Fiddle> content = repo.scoped(
				new WorkspaceId("workspace1")).openFiddle(
				new FiddleId("script1"));

		assertTrue(content.isPresent());
		assertEquals("script1 content", content.get().content);

	}

	@Test
	public void findAFiddleInCommonWorkspace() {
		final Optional<Fiddle> content = repo.scoped(
				new WorkspaceId("workspace2")).openFiddle(
				new FiddleId("script99"));

		assertTrue(content.isPresent());
		assertEquals("script99 content", content.get().content);
	}


	@Test
	public void findAFiddleThatDoesNotExist() {
		final Optional<Fiddle> content = repo.scoped(
				new WorkspaceId("workspace1")).openFiddle(
				new FiddleId("script111"));

		assertTrue(!content.isPresent());
	}
	
	@Test
	public void findAFiddleWhenWorkspaceDoesNotExist() {
		final Optional<Fiddle> content = repo.scoped(
				new WorkspaceId("workspace99")).openFiddle(
				new FiddleId("script99"));

		assertTrue(!content.isPresent());
	}

	
}
