package fiddle.repository;

import java.io.File;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.repository.impl.AbsentScopedRepository;
import fiddle.repository.impl.FiddleFileEntityManager;
import fiddle.repository.impl.FileEntityManager;
import fiddle.repository.impl.PresentScopedRepository;
import fiddle.repository.impl.TemplateFileEntityManager;

public class Repository {

	private final File dir;

	public Repository(final File dir) {
		this.dir = dir;
	}

	public ScopedRepository<FiddleId, Fiddle> fiddles(
			final WorkspaceId workspaceId) {
		return scoped(
				new FiddleFileEntityManager(dir, workspaceId),
				workspaceId,
				scoped(new FiddleFileEntityManager(dir, WorkspaceId.COMMON),
						WorkspaceId.COMMON,
						new AbsentScopedRepository<FiddleId, Fiddle>()));
	}
	
	public ScopedRepository<TemplateId, String> templates(final WorkspaceId workspaceId) {
		return scoped(
				new TemplateFileEntityManager(dir, workspaceId),
				workspaceId,
				scoped(new TemplateFileEntityManager(dir, WorkspaceId.COMMON),
						WorkspaceId.COMMON,
						new AbsentScopedRepository<TemplateId, String>()));
		
	}

	private <I, E> ScopedRepository<I, E> scoped(
			final FileEntityManager<I, E> fileManager,
			final WorkspaceId workspaceId, ScopedRepository<I, E> fallback) {
		
		final File scoped = new File(dir, workspaceId.id);

		if (scoped.exists() && scoped.isDirectory() && scoped.canRead()) {
			return new PresentScopedRepository<I, E>(fallback, fileManager);
		} else {
			return new AbsentScopedRepository<I, E>();
		}
	}
}
