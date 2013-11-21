package fiddle.repository.impl;

import java.io.IOException;

import com.google.common.base.Optional;

import fiddle.repository.ScopedRepository;

public class AbsentScopedRepository<Ident, Entity> implements ScopedRepository<Ident, Entity> {
	
	@Override
	public Optional<Entity> open(Ident ident) {
		return Optional.absent();
	}
	
	@Override
	public void clearCacheFor(Ident id) {
	}
	
	@Override
	public void write(Ident id, Entity entity) throws IOException {
	}
}
