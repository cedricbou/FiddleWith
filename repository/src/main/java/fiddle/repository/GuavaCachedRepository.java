package fiddle.repository;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaCachedRepository<ReadRsc, WriteRsc, Id> implements Repository<ReadRsc, WriteRsc, Id>, CacheableRepository<Id> {

	private final LoadingCache<Id, Optional<ReadRsc>> cache;
	
	private final Repository<ReadRsc, WriteRsc, Id> repoToCache;
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	public GuavaCachedRepository(final Repository<ReadRsc, WriteRsc, Id> repoToCache, final ResourceLoader<ReadRsc, Id> loader, final int size, final int expireInMinutes) {
		this.repoToCache = repoToCache;
		this.cache = CacheBuilder
			.newBuilder().maximumSize(size).expireAfterWrite(expireInMinutes, TimeUnit.MINUTES)
			.build(new CacheLoader<Id, Optional<ReadRsc>>() {
				@Override
				public Optional<ReadRsc> load(Id id) {
					return loader.load(id);
				}
			});
	}
	
	@Override
	public Optional<ReadRsc> open(Id id) {
		try {
			return cache.get(id);
		} catch (ExecutionException e) {
			LOG.warn("Failed to load resource from cache, will return absent resource", e);
			return Optional.absent();
		}
	}
	
	@Override
	public void write(Id id, WriteRsc rsc) throws IOException {
		try {
			repoToCache.write(id, rsc);
			clearCacheFor(id);
		}
		catch(IOException e) {
			LOG.error("Failed to write resource from cache repository", e);
			throw e;
		}
	}
	
	@Override
	public void clearCacheFor(Id id) {
		cache.invalidate(id);
	}
	
}
