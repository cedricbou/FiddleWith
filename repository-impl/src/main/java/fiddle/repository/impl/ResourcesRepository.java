package fiddle.repository.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.Mustache;
import com.google.common.base.Optional;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.validation.Validator;

import fiddle.api.TemplateId;
import fiddle.config.ResourceConfiguration;
import fiddle.config.ResourceFileName;
import fiddle.resources.Resources;
import fiddle.repository.Repository;
import fiddle.resources.FallbackResources;
import fiddle.resources.WorkspaceResources;

public class ResourcesRepository implements
		Repository<Resources, String, ResourceFileName> {

	private final File repo;

	private final Repository<Mustache, String, TemplateId> templates;
	private final Repository<Mustache, String, TemplateId> commonTemplates;
	
	private final Environment env;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public ResourcesRepository(final File repo,
			final Repository<Mustache, String, TemplateId> templates,
			final Repository<Mustache, String, TemplateId> commonTemplates,
			final Environment env) {
		this.env = env;
		this.repo = repo;
		this.templates = templates;
		this.commonTemplates = commonTemplates;
	}

	@Override
	public Optional<Resources> open(ResourceFileName id) {
		@SuppressWarnings("static-access")
		final File rf = new File(repo, id.name);

		final ConfigurationFactory<ResourceConfiguration> configurationFactory = ConfigurationFactory
				.forClass(ResourceConfiguration.class, new Validator());

		if (rf.exists() && rf.canRead() && rf.isFile()) {
			try {
				final ResourceConfiguration config = configurationFactory
						.build(rf);

				return Optional.of((Resources) new FallbackResources(
						new WorkspaceResources(templates, config, env),
						new WorkspaceResources(commonTemplates, config, env)));
			} catch (Exception e) {
				LOG.error("failed to load resources configuration file in "
						+ repo.getName());
				return Optional.absent();
			}
		}

		return Optional.absent();
	}

	@Override
	public void write(ResourceFileName id, String rsc) throws IOException {

	}
}
