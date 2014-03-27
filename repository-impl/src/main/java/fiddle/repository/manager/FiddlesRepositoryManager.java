package fiddle.repository.manager;

import java.io.File;

import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.repository.FallbackRepository;
import fiddle.repository.GuavaCachedRepository;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;
import fiddle.repository.RepositoryResourceLoader;
import fiddle.repository.impl.FiddleRepository;

public class FiddlesRepositoryManager extends AbstractRepositoryManager
		implements RepositoryManager<Repository<Fiddle, Fiddle, FiddleId>>,
		FiddlesRepositoryManagerTools {

	public FiddlesRepositoryManager(final File repoFolder) {
		super(repoFolder);
	}

	public final Repository<Fiddle, Fiddle, FiddleId> repo(final WorkspaceId id) {
		final Repository<Fiddle, Fiddle, FiddleId> main = new FiddleRepository(
				new File(repoFolder(), id.id));

		final Repository<Fiddle, Fiddle, FiddleId> common = new FiddleRepository(
				new File(repoFolder(), WorkspaceId.COMMON.id));

		final FallbackRepository<Fiddle, Fiddle, FiddleId> combined = new FallbackRepository<Fiddle, Fiddle, FiddleId>(
				main, common);

		return new GuavaCachedRepository<Fiddle, Fiddle, FiddleId>(
				combined,
				new RepositoryResourceLoader<Fiddle, Fiddle, FiddleId>(combined),
				50, 5);
	}
	
	@Override
	public void visit(FiddleVisitor visitor) {
		for(final WorkspaceId wId : workspaces()) {
			final Repository<Fiddle, Fiddle, FiddleId> repo = repo(wId);
			for(final FiddleId id :repo.ids()) {
				final Optional<Fiddle> found = repo.open(id);
				if(found.isPresent()) {
					visitor.visit(wId, id, found.get());
				}
			}
		}
	}
	
	public static interface FiddleVisitor {
		public void visit(final WorkspaceId wid, final FiddleId fid, final Fiddle fiddle);
	}
}
