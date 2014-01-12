package fiddle.repository.impl;

import java.io.IOException;

import com.google.common.base.Optional;

import fiddle.repository.ScopedRepository;

public class AbsentScopedRepository<Ident, ReadEntity, WriteEntity> implements ScopedRepository<Ident, ReadEntity, WriteEntity> {
	
	@Override
	public Optional<ReadEntity> open(Ident ident) {
		return Optional.absent();
	}
	
	@Override
	public void clearCacheFor(Ident id) {
	}
	
	@Override
	public void write(Ident id, WriteEntity entity) throws IOException {
	}
}
