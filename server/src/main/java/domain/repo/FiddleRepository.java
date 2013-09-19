package domain.repo;

import java.io.File;

import com.google.common.base.Optional;

import domain.FiddleEnvironment;
import domain.WorkspaceId;

public class FiddleRepository {

	private final FiddleEnvironment env;

	private final File fiddleRepository;

	public FiddleRepository(final File fiddleRepository, final FiddleEnvironment env) {
		this.env = env;
		this.fiddleRepository = fiddleRepository;
	}

	public Optional<FiddleWorkspace> find(final WorkspaceId wId) {
		final File wk = new File(fiddleRepository, wId.toString());
		if(wk.exists() && wk.canRead()) {
			return Optional.of(new FiddleWorkspace(wId, wk, env));
		}
		return Optional.absent();
	}

}
