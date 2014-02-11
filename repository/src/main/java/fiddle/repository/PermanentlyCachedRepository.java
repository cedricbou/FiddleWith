package fiddle.repository;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class PermanentlyCachedRepository<ReadRsc, WriteRsc, Id> implements Repository<ReadRsc, WriteRsc, Id>, CacheableRepository<Id> {

	private final Map<Id, ReadRsc> cache = Maps.newHashMap();
	
	private final Repository<ReadRsc, WriteRsc, Id> repoToCache;
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	public PermanentlyCachedRepository(final Repository<ReadRsc, WriteRsc, Id> repoToCache) {
		this.repoToCache = repoToCache;
	}

	@Override
	public void clearCacheFor(Id id) {
		throw new IllegalAccessError("clearing cache for permanently cached repository is not supported yet");
	}
	
	@Override
	public Optional<ReadRsc> open(Id id) {
		if(!cache.containsKey(id) || null == cache.get(id)) {
			LOG.debug("setting permanent cache for {}", id);
			cache.put(id, repoToCache.open(id).orNull());
		}
		
		LOG.debug("read from permanent cache for {}", id);
		return Optional.fromNullable(cache.get(id));
	}
	
	@Override
	public void write(Id id, WriteRsc rsc) throws IOException {
		throw new IllegalAccessError("write in permanently cached repository is not supported yet");
	}
}
