package fiddle.repository;

import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;

public interface ScopedFiddleRepository {

	public Optional<Fiddle> openFiddle(final FiddleId fiddleId);
}
