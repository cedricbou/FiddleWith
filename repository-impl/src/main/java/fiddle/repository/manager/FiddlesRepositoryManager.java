package fiddle.repository.manager;

import java.io.File;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.FallbackRepository;
import fiddle.repository.GuavaCachedRepository;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;
import fiddle.repository.RepositoryResourceLoader;
import fiddle.repository.impl.FiddleRepository;

public class FiddlesRepositoryManager implements RepositoryManager<Repository<Fiddle, Fiddle, FiddleId>> {

	private final File repoFolder;

	public FiddlesRepositoryManager(final File repoFolder) {
		this.repoFolder = repoFolder;
	}

	public final Repository<Fiddle, Fiddle, FiddleId> repo(
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
}
