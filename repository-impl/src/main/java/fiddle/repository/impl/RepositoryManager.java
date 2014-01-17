package fiddle.repository.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.Mustache;
import com.yammer.dropwizard.config.Environment;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.FallbackRepository;
import fiddle.repository.GuavaCachedRepository;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryResourceLoader;
import fiddle.resources.Resources;

public class RepositoryManager {

	private final File repoFolder;

	private final Map<WorkspaceId, ResourcesRepository> resourcesCache = new HashMap<WorkspaceId, ResourcesRepository>();

	private final Environment env;
	
	public RepositoryManager(final File repoFolder, final Environment env) {
		this.repoFolder = repoFolder;
		this.env = env;
	}

	public final Repository<Fiddle, Fiddle, FiddleId> fiddles(
			final WorkspaceId id) {
		final Repository<Fiddle, Fiddle, FiddleId> main = new FiddleRepository(
				new File(repoFolder, id.id));

		final Repository<Fiddle, Fiddle, FiddleId> common = new FiddleRepository(
				new File(repoFolder, WorkspaceId.COMMON.id));

		final FallbackRepository<Fiddle, Fiddle, FiddleId> combined = new FallbackRepository<Fiddle, Fiddle, FiddleId>(
				main, common);

		return new GuavaCachedRepository<Fiddle, Fiddle, FiddleId>(
				combined,
				new RepositoryResourceLoader<Fiddle, Fiddle, FiddleId>(combined),
				50, 5);
	}

	public final Repository<Mustache, String, TemplateId> templates(
			final WorkspaceId id) {
		final Repository<Mustache, String, TemplateId> combined = new TemplateRepository(
				new File(repoFolder, id.id), new File(repoFolder,
						WorkspaceId.COMMON.id));

		return new GuavaCachedRepository<Mustache, String, TemplateId>(
				combined,
				new RepositoryResourceLoader<Mustache, String, TemplateId>(
						combined), 50, 5);
	}

	public final Repository<Resources, String, ResourceFileName> resources(
			final WorkspaceId id) {

		if (!resourcesCache.containsKey(id)) {
			final ResourcesRepository combined = new ResourcesRepository(
					new File(repoFolder, id.id), this.templates(id),
					this.templates(WorkspaceId.COMMON), env);

			resourcesCache.put(id, combined);
		}
		
		return resourcesCache.get(id);
	}

}
