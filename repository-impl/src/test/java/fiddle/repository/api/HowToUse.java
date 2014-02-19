package fiddle.repository.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.config.http.ConfiguredHttpConfiguration;
import fiddle.dbi.DecoratedDbi;
import fiddle.httpclient.ConfiguredFiddleHttpClient;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.repository.Repository;
import fiddle.repository.manager.FiddlesRepositoryManager;
import fiddle.repository.manager.RepositoryManager;
import fiddle.repository.manager.ResourcesRepositoryManager;
import fiddle.repository.manager.TemplatesRepositoryManager;
import fiddle.resources.Resources;
import fiddle.resources.builder.DbiResourceBuilder;
import fiddle.resources.builder.HttpResourceBuilder;

public class HowToUse {

	private final RepositoryManager repo;

	private final RepositoryManager repoNoFallback;

	private final File ws2;

	private final HttpResourceBuilder httpBuilder = mock(HttpResourceBuilder.class);
	private final DbiResourceBuilder dbiBuilder = mock(DbiResourceBuilder.class);

	private final DBI fooDbi = mock(DBI.class);
	private final DBI barDbi = mock(DBI.class);
	
	private final ConfiguredFiddleHttpClient fooHttp = mock(ConfiguredFiddleHttpClient.class);
	private final ConfiguredFiddleHttpClient barHttp = mock(ConfiguredFiddleHttpClient.class);

	public HowToUse() {
		final URL url = this.getClass().getResource("localised.donoterase");
		final File globalDir = new File(url.getFile()).getParentFile();
		final File repoDir = new File(globalDir, "repository");
		final File repoDirNoFB = new File(globalDir, "repositorynofallback");
		
		final WorkspaceId wId = new WorkspaceId("workspace2");
		ws2 = new File(repoDir, wId.id);

		when(
				httpBuilder
						.buildFromConfig(any(WorkspaceId.class), eq("foo"), any(ConfiguredHttpConfiguration.class)))
				.thenReturn(
						fooHttp);

		when(
				httpBuilder
						.buildFromConfig(any(WorkspaceId.class), eq("bar"), any(ConfiguredHttpConfiguration.class)))
				.thenReturn(
						barHttp);

		when(
				httpBuilder
						.buildFromConfig(eq(wId), eq("meteo"), any(ConfiguredHttpConfiguration.class)))
				.thenReturn(
						new FiddleHttpClient(new HttpClientBuilder().using(
								new HttpClientConfiguration()).build())
								.withUrl("http://localhost:8089"));
		
		when(
				dbiBuilder.buildDbiFromConfig(eq("foo"),
						any(DatabaseConfiguration.class))).thenReturn(fooDbi);
		when(
				dbiBuilder.buildDbiFromConfig(eq("bar"),
						any(DatabaseConfiguration.class))).thenReturn(barDbi);

		repo = new RepositoryManager(new FiddlesRepositoryManager(repoDir), new TemplatesRepositoryManager(repoDir), new ResourcesRepositoryManager(repoDir, dbiBuilder, httpBuilder));
		repoNoFallback = new RepositoryManager(new FiddlesRepositoryManager(repoDirNoFB), new TemplatesRepositoryManager(repoDirNoFB), new ResourcesRepositoryManager(repoDirNoFB, dbiBuilder, httpBuilder));
	}

	@Test
	public void findAFiddleInItsWorkspace() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace1")).open(new FiddleId("script1"));

		assertTrue(content.isPresent());
		assertEquals("script1 content", content.get().content);

	}

	@Test
	public void findAFiddleInItsWorkspaceWithNoFallback() {
		final Optional<Fiddle> content = repoNoFallback.fiddles(
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
				new WorkspaceId("workspace1"))
				.open(new TemplateId("template2"));

		assertTrue(content.isPresent());

		final StringWriter writer = new StringWriter();
		content.get().execute(writer, ImmutableMap.of());

		assertEquals("the template in common.", writer.getBuffer().toString());
	}

	@Test
	public void findATemplateExistingInWorkspace() {
		final Optional<Mustache> content = repo.templates(
				new WorkspaceId("workspace1"))
				.open(new TemplateId("template1"));

		assertTrue(content.isPresent());

		final StringWriter writer = new StringWriter();
		content.get().execute(writer, ImmutableMap.of());

		assertEquals("a template in workspace 1.", writer.getBuffer()
				.toString());
	}

	@Test
	public void findATemplateExistingInWorkspaceWithNoFallback() {
		final Optional<Mustache> content = repoNoFallback.templates(
				new WorkspaceId("workspace1"))
				.open(new TemplateId("template1"));

		assertTrue(content.isPresent());

		final StringWriter writer = new StringWriter();
		content.get().execute(writer, ImmutableMap.of());

		assertEquals("a template in workspace 1.", writer.getBuffer()
				.toString());
	}

	@Test
	public void findAFiddleThatDoesNotExist() {
		final Optional<Fiddle> content = repo.fiddles(
				new WorkspaceId("workspace1")).open(new FiddleId("script111"));

		assertTrue(!content.isPresent());
	}

	@Test
	public void findAFiddleThatDoesNotExistInRepoWithNoFallback() {
		final Optional<Fiddle> content = repoNoFallback.fiddles(
				new WorkspaceId("workspace1")).open(new FiddleId("script111"));

		assertTrue(!content.isPresent());
	}

	@Test
	public void findATemplateThatDoesNotExist() {
		final Optional<Mustache> content = repo.templates(
				new WorkspaceId("workspace1")).open(
				new TemplateId("template1111"));

		assertTrue(!content.isPresent());
	}

	@Test
	public void findATemplateThatDoesNotExistInRepoWithNoFallback() {
		final Optional<Mustache> content = repoNoFallback.templates(
				new WorkspaceId("workspace1")).open(
				new TemplateId("template1111"));

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

		repo.templates(new WorkspaceId("workspace2")).write(
				new TemplateId("titi"), "titi is in the template.");

		final Optional<Mustache> res = repo.templates(
				new WorkspaceId("workspace2")).open(new TemplateId("titi"));

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

	@Test
	public void findResourcesInRepo() {
		final Optional<Resources> optRsc = repo.resources(
				new WorkspaceId("workspace1")).open(ResourceFileName.VALUE);

		assertTrue(optRsc.isPresent());

		final DecoratedDbi decoFoo = optRsc.get().dbi("foo");
		assertEquals(fooDbi, decoFoo.undecorated());

		final DecoratedDbi deboBar = optRsc.get().dbi("bar");
		assertEquals(barDbi, deboBar.undecorated());

		try {
			optRsc.get().dbi("bar2");
			fail("if no dbi found, should throw an exception");
		} catch (Exception e) {
			assertTrue(true);
		}

		final ConfiguredFiddleHttpClient myHttpFoo = optRsc.get().http("foo");
		assertEquals(fooHttp, myHttpFoo);

		final ConfiguredFiddleHttpClient myHttpBar = optRsc.get().http("bar");
		assertEquals(barHttp, myHttpBar);

		try {
			optRsc.get().http("bar2");
			fail("if no http found, should throw an exception");
		} catch (Exception e) {
			assertTrue(true);
		}
		
	}
	
	@Test
	public void verifyRepoIds() {
		final WorkspaceId wId = new WorkspaceId("workspace1");
		
		assertEquals(ImmutableList.of(new FiddleId("script1")), repo.fiddles(wId).ids());
		assertEquals(ImmutableList.of(new TemplateId("template1")), repo.templates(wId).ids());
		assertEquals(ImmutableList.of(ResourceFileName.VALUE), repo.resources(wId).ids());
	}
}
