package fiddle.repository.manager;

import java.io.File;
import java.util.List;

import fiddle.api.WorkspaceId;
import fiddle.repository.utils.WorkspaceFinder;

public abstract class AbstractRepositoryManager {

	private final File repoFolder;
	
	protected File repoFolder() {
		return repoFolder;
	}
	
	public AbstractRepositoryManager(final File repoFolder) {
		this.repoFolder = repoFolder;
	}
	
	public List<WorkspaceId> workspaces() {
		return new WorkspaceFinder(this.repoFolder).workspaces();
	}
}
