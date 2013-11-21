package fiddle.repository;

import java.io.IOException;

import com.google.common.base.Optional;

public interface ScopedRepository<Ident, Entity> {

	public Optional<Entity> open(final Ident id);
	
	public void clearCacheFor(final Ident id);
	
	public void write(final Ident ident, final Entity entity) throws IOException;
}
