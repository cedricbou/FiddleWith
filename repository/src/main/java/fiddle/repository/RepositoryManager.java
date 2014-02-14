package fiddle.repository;

import java.util.List;

import fiddle.api.WorkspaceId;


public interface RepositoryManager<R extends Repository<?, ?, ?>> {

	public abstract R repo(WorkspaceId id);
	
	public List<WorkspaceId> workspaces();

}