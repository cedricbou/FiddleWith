package fiddle.repository;

public interface CacheableRepository<Id> {

	public void clearCacheFor(final Id id);
}
