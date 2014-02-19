package fiddle.repository;

import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public interface Repository<ReadRsc, WriteRsc, Id> {

	public Optional<ReadRsc> open(final Id id);
	
	public void write(final Id id, final WriteRsc rsc) throws IOException;
	
	public ImmutableList<Id> ids();

}
