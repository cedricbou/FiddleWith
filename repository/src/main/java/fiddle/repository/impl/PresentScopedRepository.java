package fiddle.repository.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fiddle.repository.ScopedRepository;

public class PresentScopedRepository<Ident, Entity> implements
		ScopedRepository<Ident, Entity> {

	private static final Logger LOG = LoggerFactory
			.getLogger(PresentScopedRepository.class);

	private final ScopedRepository<Ident, Entity> common;

	private final FileEntityManager<Ident, Entity> fileManager;

	private final LoadingCache<Ident, Optional<Entity>> cache = CacheBuilder
			.newBuilder().maximumSize(50).expireAfterWrite(5, TimeUnit.MINUTES)
			.build(new CacheLoader<Ident, Optional<Entity>>() {
				@Override
				public Optional<Entity> load(Ident id) {
					return uncachedOpenFiddle(id);
				}
			});

	public PresentScopedRepository(
			final ScopedRepository<Ident, Entity> common,
			final FileEntityManager<Ident, Entity> fileManager) {
		this.common = common;
		this.fileManager = fileManager;
	}

	@Override
	public Optional<Entity> open(Ident id) {
		try {
			return cache.get(id);
		} catch (ExecutionException e) {
			LOG.warn("failed to get file from cache, will return absent", e);
			return Optional.absent();
		}
	}

	private Optional<Entity> uncachedOpenFiddle(Ident ident) {
		return fileManager.find(ident).or(common.open(ident));
	}

	@Override
	public void clearCacheFor(Ident id) {
		cache.invalidate(id);
	}

	@Override
	public void write(Ident ident, Entity entity) throws IOException {
		try {
			fileManager.write(ident, entity);
			clearCacheFor(ident);
		} catch (IOException ioe) {
			throw ioe;
		}
	}
}
