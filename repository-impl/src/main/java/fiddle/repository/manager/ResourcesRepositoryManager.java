package fiddle.repository.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;
import fiddle.repository.impl.ResourcesRepository;
import fiddle.resources.Resources;
import fiddle.resources.builder.DbiResourceBuilder;
import fiddle.resources.builder.HttpResourceBuilder;

public class ResourcesRepositoryManager implements
		RepositoryManager<Repository<Resources, String, ResourceFileName>> {

	private final File repoFolder;

	private final Map<WorkspaceId, ResourcesRepository> resourcesCache = new HashMap<WorkspaceId, ResourcesRepository>();

	private final HttpResourceBuilder httpBuilder;
	private final DbiResourceBuilder dbiBuilder;

	public ResourcesRepositoryManager(final File repoFolder,
			final DbiResourceBuilder dbiBuilder,
			final HttpResourceBuilder httpBuilder) {
		this.repoFolder = repoFolder;
		this.httpBuilder = httpBuilder;
		this.dbiBuilder = dbiBuilder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fiddle.repository.manager.RepositoryManager2#repo(fiddle.api.WorkspaceId)
	 */
	@Override
	public final Repository<Resources, String, ResourceFileName> repo(
			final WorkspaceId id) {

		if (!resourcesCache.containsKey(id)) {
			final ResourcesRepository combined = new ResourcesRepository(id,
					new File(repoFolder, id.id), new File(repoFolder,
							WorkspaceId.COMMON.id), dbiBuilder, httpBuilder);

			resourcesCache.put(id, combined);
		}

		return resourcesCache.get(id);
	}
}
