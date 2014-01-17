package fiddle.repository;

import java.io.IOException;

import com.google.common.base.Optional;

public class FallbackRepository<ReadRsc, WriteRsc, Id> implements Repository<ReadRsc, WriteRsc, Id> {

	private final Repository<ReadRsc, WriteRsc, Id> main;
	private final Repository<ReadRsc, WriteRsc, Id> fallback;
	
	public FallbackRepository(final Repository<ReadRsc, WriteRsc, Id> main, final Repository<ReadRsc, WriteRsc, Id> fallback) {
		this.main = main;
		this.fallback = fallback;
	}
	
	@Override
	public Optional<ReadRsc> open(Id id) {
		return main.open(id).or(fallback.open(id));
	}
	
	@Override
	public void write(Id id, WriteRsc rsc) throws IOException {
		main.write(id, rsc);
	}
	
}
