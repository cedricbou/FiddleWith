package fiddle.repository.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.PermanentlyCachedRepository;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;
import fiddle.repository.impl.ResourcesRepository;
import fiddle.resources.Resources;
import fiddle.resources.builder.DbiResourceBuilder;
import fiddle.resources.builder.HttpResourceBuilder;

public class ResourcesRepositoryManager extends AbstractRepositoryManager implements
		RepositoryManager<Repository<Resources, String, ResourceFileName>>, ResourcesRepositoryManagerTools {

	private final Logger LOG = LoggerFactory.getLogger(ResourcesRepositoryManager.class);
	
	private final Map<WorkspaceId, Repository<Resources, String, ResourceFileName>> resourcesCache = new HashMap<WorkspaceId, Repository<Resources, String, ResourceFileName>>();

	private final HttpResourceBuilder httpBuilder;
	private final DbiResourceBuilder dbiBuilder;

	public ResourcesRepositoryManager(final File repoFolder,
			final DbiResourceBuilder dbiBuilder,
			final HttpResourceBuilder httpBuilder) {
		super(repoFolder);
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
			final Repository<Resources, String, ResourceFileName> combined = new PermanentlyCachedRepository<Resources, String, ResourceFileName>(new ResourcesRepository(id,
					new File(repoFolder(), id.id), new File(repoFolder(),
							WorkspaceId.COMMON.id), dbiBuilder, httpBuilder));

			resourcesCache.put(id, combined);
		}

		return resourcesCache.get(id);
	}
	
	@Override
	public void preload() {
		LOG.info("preloading resources...");
		for(final WorkspaceId id : workspaces()) {
			LOG.info(" - preloading resources for workspace {}", id);
			repo(id).open(ResourceFileName.VALUE);
		}
		LOG.info("resources prealoded!");
	}
	
}
