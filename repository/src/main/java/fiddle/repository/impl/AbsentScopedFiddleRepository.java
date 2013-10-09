package fiddle.repository.impl;

import com.google.common.base.Optional;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.repository.ScopedFiddleRepository;

public class AbsentScopedFiddleRepository implements ScopedFiddleRepository {
	
	@Override
	public Optional<Fiddle> openFiddle(FiddleId fiddleId) {
		return Optional.absent();
	}
}
