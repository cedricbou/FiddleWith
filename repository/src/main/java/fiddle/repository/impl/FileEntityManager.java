package fiddle.repository.impl;

import java.io.IOException;

import com.google.common.base.Optional;

public interface FileEntityManager<Ident, ReadEntity, WriteEntity> {

	public Optional<ReadEntity> find(Ident id);
		
	public void write(final Ident ident, final WriteEntity entity) throws IOException;
}
