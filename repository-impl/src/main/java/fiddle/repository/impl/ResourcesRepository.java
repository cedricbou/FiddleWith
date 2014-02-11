package fiddle.repository.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

import fiddle.api.WorkspaceId;
import fiddle.config.ResourceConfiguration;
import fiddle.config.ResourceFileName;
import fiddle.repository.Repository;
import fiddle.resources.FallbackResources;
import fiddle.resources.Resources;
import fiddle.resources.WorkspaceResources;
import fiddle.resources.builder.DbiResourceBuilder;
import fiddle.resources.builder.HttpResourceBuilder;

public class ResourcesRepository implements
		Repository<Resources, String, ResourceFileName> {

	private final File repo;
	private final File repoFB;

	private final HttpResourceBuilder httpBuilder;
	private final DbiResourceBuilder dbiBuilder;

	private final WorkspaceId wId;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public ResourcesRepository(final WorkspaceId wId, final File repo,
			final File fallbackRepo, final DbiResourceBuilder dbiBuilder,
			final HttpResourceBuilder httpBuilder) {
		this.wId = wId;
		this.dbiBuilder = dbiBuilder;
		this.httpBuilder = httpBuilder;
		this.repo = repo;
		this.repoFB = fallbackRepo;
	}

	@Override
	public Optional<Resources> open(ResourceFileName id) {
		@SuppressWarnings("static-access")
		final File rf = new File(repo, id.name);

		@SuppressWarnings("static-access")
		final File rfFB = new File(repoFB, id.name);

		final ConfigurationFactory<ResourceConfiguration> configurationFactory = ConfigurationFactory
				.forClass(ResourceConfiguration.class, new Validator());

		if (rf.exists() && rf.canRead() && rf.isFile()) {
			try {
				final ResourceConfiguration config = configurationFactory
						.build(rf);

				if (rfFB.exists() && rfFB.canRead() && rfFB.isFile()) {
					final ResourceConfiguration configFallback = configurationFactory
							.build(rfFB);

					return Optional.of((Resources) new FallbackResources(
							new WorkspaceResources(wId, config, dbiBuilder,
									httpBuilder), new WorkspaceResources(wId,
									configFallback, dbiBuilder, httpBuilder)));
				} else {
					return Optional.of((Resources) new WorkspaceResources(wId, config,
							dbiBuilder, httpBuilder));
				}
			} catch (Exception e) {
				LOG.error("failed to load resources configuration file in "
						+ repo.getName(), e);
				return Optional.absent();
			}
		}

		return Optional.absent();
	}

	@Override
	public void write(ResourceFileName id, String rsc) throws IOException {

	}
}
