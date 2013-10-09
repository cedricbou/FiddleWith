package fiddle.repository;

import java.io.File;

import fiddle.api.WorkspaceId;
import fiddle.repository.impl.AbsentScopedFiddleRepository;
import fiddle.repository.impl.PresentScopedFiddleRepository;

public class FiddleRepository {

	private final File dir;

	public FiddleRepository(final File dir) {
		this.dir = dir;
	}

	public ScopedFiddleRepository scoped(final WorkspaceId workspaceId) {
		return scoped(workspaceId, scoped(WorkspaceId.COMMON, new AbsentScopedFiddleRepository()));
	}
	
	private ScopedFiddleRepository scoped(final WorkspaceId workspaceId, ScopedFiddleRepository fallback) {
		final File scoped = new File(dir, workspaceId.id);

		if (scoped.exists() && scoped.isDirectory() && scoped.canRead()) {
			return new PresentScopedFiddleRepository(workspaceId, scoped, fallback);
		} else {
			return new AbsentScopedFiddleRepository();
		}
	}

}
