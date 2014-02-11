package fiddle.repository;

import fiddle.api.WorkspaceId;


public interface RepositoryManager<R extends Repository<?, ?, ?>> {

	public abstract R repo(WorkspaceId id);

}