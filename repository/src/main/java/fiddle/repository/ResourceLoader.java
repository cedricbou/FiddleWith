package fiddle.repository;

import com.google.common.base.Optional;

public interface ResourceLoader<ReadRsc, Id> {

	public Optional<ReadRsc> load(final Id id);
}
