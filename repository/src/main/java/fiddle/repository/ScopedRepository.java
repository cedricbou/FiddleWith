package fiddle.repository;

import java.io.IOException;

import com.google.common.base.Optional;

public interface ScopedRepository<Ident, ReadEntity, WriteEntity> {

	public Optional<ReadEntity> open(final Ident id);
	
	public void clearCacheFor(final Ident id);
	
	public void write(final Ident ident, final WriteEntity entity) throws IOException;

}
