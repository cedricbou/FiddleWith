package fiddle.repository;

import com.google.common.base.Optional;

public class RepositoryResourceLoader<ReadRsc, WriteRsc, Id> implements ResourceLoader<ReadRsc, Id> {

	private final Repository<ReadRsc, WriteRsc, Id> repo;
	
	public RepositoryResourceLoader(final Repository<ReadRsc, WriteRsc, Id> repo) {
		this.repo = repo;
	}

	@Override
	public Optional<ReadRsc> load(Id id) {
		return repo.open(id);
	}
}
