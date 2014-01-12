package fiddle.repository;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.mustachejava.Mustache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.config.FiddleDBIFactory;
import fiddle.config.Resources;
import fiddle.repository.impl.AbsentScopedRepository;
import fiddle.repository.impl.FiddleFileEntityManager;
import fiddle.repository.impl.FileEntityManager;
import fiddle.repository.impl.PresentScopedRepository;
import fiddle.repository.impl.TemplateFileEntityManager;

public class Repository {

	private final RepositoryResourceManager repoResMgr = new RepositoryResourceManager();

	private final File dir;

	// private final static Logger LOG =
	// LoggerFactory.getLogger(Repository.class);

	private final LoadingCache<WorkspaceId, ScopedRepository<FiddleId, Fiddle, Fiddle>> fiddlesCache = CacheBuilder
			.newBuilder()
			.maximumSize(50)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build(new CacheLoader<WorkspaceId, ScopedRepository<FiddleId, Fiddle, Fiddle>>() {
				@Override
				public ScopedRepository<FiddleId, Fiddle, Fiddle> load(
						WorkspaceId workspaceId) {
					return scoped(
							new FiddleFileEntityManager(dir, workspaceId),
							workspaceId,
							scoped(new FiddleFileEntityManager(dir,
									WorkspaceId.COMMON),
									WorkspaceId.COMMON,
									new AbsentScopedRepository<FiddleId, Fiddle, Fiddle>()));
				}
			});

	private final LoadingCache<WorkspaceId, ScopedRepository<TemplateId, Mustache, String>> templatesCache = CacheBuilder
			.newBuilder()
			.maximumSize(50)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build(new CacheLoader<WorkspaceId, ScopedRepository<TemplateId, Mustache, String>>() {
				@Override
				public ScopedRepository<TemplateId, Mustache, String> load(
						WorkspaceId workspaceId) {
					return scoped(
							new TemplateFileEntityManager(dir, workspaceId, WorkspaceId.COMMON),
							workspaceId,
							scoped(new TemplateFileEntityManager(dir,
									WorkspaceId.COMMON, null),
									WorkspaceId.COMMON,
									new AbsentScopedRepository<TemplateId, Mustache, String>()));
				}
			});

	public Repository(final File dir) {
		this.dir = dir;
	}

	public Resources resources(final Resources global,
			final FiddleDBIFactory factory, final WorkspaceId workspaceId) {
		final File scoped = new File(dir, workspaceId.id);
		return repoResMgr.resources(new File(scoped, "resources.conf"), global,
				factory, workspaceId);
	}

	public ScopedRepository<FiddleId, Fiddle, Fiddle> fiddles(
			final WorkspaceId workspaceId) {
		try {
			return fiddlesCache.get(workspaceId);
		} catch (ExecutionException e) {
			throw new RuntimeException(
					"Failed to get workspace from fiddle workspaces cache", e);
		}
	}

	public ScopedRepository<TemplateId, Mustache, String> templates(
			final WorkspaceId workspaceId) {
		try {
			return templatesCache.get(workspaceId);
		} catch (ExecutionException e) {
			throw new RuntimeException(
					"Failed to get workspace from template workspaces cache", e);
		}

	}

	private <I, RE, WE> ScopedRepository<I, RE, WE> scoped(
			final FileEntityManager<I, RE, WE> fileManager,
			final WorkspaceId workspaceId, ScopedRepository<I, RE, WE> fallback) {

		final File scoped = new File(dir, workspaceId.id);

		if (scoped.exists() && scoped.isDirectory() && scoped.canRead()) {
			return new PresentScopedRepository<I, RE, WE>(fallback, fileManager);
		} else {
			return new AbsentScopedRepository<I, RE, WE>();
		}
	}
}
