package fiddle.repository.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.repository.Repository;
import fiddle.repository.impl.RepositoryManager;

public class HowToUse {

	private final RepositoryManager repo;

	private final File ws2;

	public HowToUse() {
		URL url = this.getClass().getResource("localised.donoterase");
		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");
		ws2 = new File(repoDir, "workspace2");
		repo = new RepositoryManager(repoDir, null);
	}

	@Test
	public void findAFiddleInItsWorkspace() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace1")).open(new FiddleId("script1"));

		assertTrue(content.isPresent());
		assertEquals("script1 content", content.get().content);

	}

	@Test
	public void findAFiddleInCommonWorkspace() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace2")).open(new FiddleId("script99"));

		assertTrue(content.isPresent());
		assertEquals("script99 content", content.get().content);
	}

	@Test
	public void findATemplateInCommonWorkspace() {
		final Optional<Mustache> content = repo.templates(
				new WorkspaceId("workspace1")).open(new TemplateId("template2"));

		assertTrue(content.isPresent());
		
		final StringWriter writer = new StringWriter();
		content.get().execute(writer, ImmutableMap.of());
		
		assertEquals("the template in common.", writer.getBuffer().toString());
	}

	@Test
	public void findATemplateExistingInWorkspace() {
		final Optional<Mustache> content = repo.templates(
				new WorkspaceId("workspace1")).open(new TemplateId("template1"));

		assertTrue(content.isPresent());
		
		final StringWriter writer = new StringWriter();
		content.get().execute(writer, ImmutableMap.of());
		
		assertEquals("a template in workspace 1.", writer.getBuffer().toString());
	}

	
	@Test
	public void findAFiddleThatDoesNotExist() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace1")).open(new FiddleId("script111"));

		assertTrue(!content.isPresent());
	}

	@Test
	public void findAFiddleWhenWorkspaceDoesNotExist() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace99")).open(new FiddleId("script99"));

		assertTrue(content.isPresent());
	}

	@Test
	public void writeANewFiddle() throws IOException {
		final File f = new File(ws2, "toto.rb");
		f.delete();

		assertFalse(f.exists());

		final Optional<Fiddle> preres = repo.fiddles(
				new WorkspaceId("workspace2")).open(new FiddleId("toto"));

		assertFalse(preres.isPresent());

		repo.fiddles(new WorkspaceId("workspace2")).write(new FiddleId("toto"),
				new Fiddle("toto is here", Language.RUBY));

		final Optional<Fiddle> res = repo
				.fiddles(new WorkspaceId("workspace2")).open(
						new FiddleId("toto"));

		assertTrue(res.isPresent());
		assertEquals("toto is here", res.get().content);
	}

	@Test
	public void writeANewTemplate() throws IOException {
		final File f = new File(ws2, "titi.mustache");
		f.delete();

		assertFalse(f.exists());

		final Optional<Mustache> preres = repo.templates(
				new WorkspaceId("workspace2")).open(new TemplateId("titi"));

		assertFalse(preres.isPresent());

		repo.templates(new WorkspaceId("workspace2")).write(new TemplateId("titi"),
				"titi is in the template.");

		final Optional<Mustache> res = repo
				.templates(new WorkspaceId("workspace2")).open(
						new TemplateId("titi"));

		assertTrue(res.isPresent());
		
		final StringWriter writer = new StringWriter();
		res.get().execute(writer, ImmutableMap.of());
		
		assertEquals("titi is in the template.", writer.getBuffer().toString());
	}

	@Test
	public void writeOverAnExistingFiddle() throws IOException {
		final File f = new File(ws2, "toto.rb");
		f.delete();

		final Repository<Fiddle, Fiddle, FiddleId> repo = this.repo
				.fiddles(new WorkspaceId("workspace2"));

		assertFalse(f.exists());

		repo.write(new FiddleId("toto"), new Fiddle("toto is here",
				Language.RUBY));

		final Optional<Fiddle> preres = repo.open(new FiddleId("toto"));

		assertTrue(preres.isPresent());
		assertEquals("toto is here", preres.get().content);

		repo.write(new FiddleId("toto"), new Fiddle(
				"toto is here, on the road again", Language.RUBY));

		final Optional<Fiddle> res = repo.open(new FiddleId("toto"));

		assertTrue(res.isPresent());
		assertEquals("toto is here, on the road again", res.get().content);
	}

}
