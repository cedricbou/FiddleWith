package fiddle.repository.impl;

import java.io.IOException;

import com.google.common.base.Optional;

public interface FileEntityManager<Ident, Entity> {

	public Optional<Entity> find(Ident id);
		
	public void write(final Ident ident, final Entity entity) throws IOException;
}
