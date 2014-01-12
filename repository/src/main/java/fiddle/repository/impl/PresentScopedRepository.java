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

public class PresentScopedRepository<Ident, ReadEntity, WriteEntity> implements
		ScopedRepository<Ident, ReadEntity, WriteEntity> {

	private static final Logger LOG = LoggerFactory
			.getLogger(PresentScopedRepository.class);

	private final ScopedRepository<Ident, ReadEntity, WriteEntity> common;

	private final FileEntityManager<Ident, ReadEntity, WriteEntity> fileManager;

	private final LoadingCache<Ident, Optional<ReadEntity>> cache = CacheBuilder
			.newBuilder().maximumSize(50).expireAfterWrite(5, TimeUnit.MINUTES)
			.build(new CacheLoader<Ident, Optional<ReadEntity>>() {
				@Override
				public Optional<ReadEntity> load(Ident id) {
					return uncachedOpenFiddle(id);
				}
			});

	public PresentScopedRepository(
			final ScopedRepository<Ident, ReadEntity, WriteEntity> common,
			final FileEntityManager<Ident, ReadEntity, WriteEntity> fileManager) {
		this.common = common;
		this.fileManager = fileManager;
	}

	@Override
	public Optional<ReadEntity> open(Ident id) {
		try {
			return cache.get(id);
		} catch (ExecutionException e) {
			LOG.warn("failed to get file from cache, will return absent", e);
			return Optional.absent();
		}
	}

	private Optional<ReadEntity> uncachedOpenFiddle(Ident ident) {
		return fileManager.find(ident).or(common.open(ident));
	}

	@Override
	public void clearCacheFor(Ident id) {
		cache.invalidate(id);
	}

	@Override
	public void write(Ident ident, WriteEntity entity) throws IOException {
		try {
			fileManager.write(ident, entity);
			clearCacheFor(ident);
		} catch (IOException ioe) {
			throw ioe;
		}
	}
}
