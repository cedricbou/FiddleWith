package fiddle.repository;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

import fiddle.api.WorkspaceId;
import fiddle.config.FiddleDBIFactory;
import fiddle.config.ResourceConfiguration;
import fiddle.config.Resources;

public class RepositoryResourceManager {

	private final static Logger LOG = LoggerFactory
			.getLogger(RepositoryResourceManager.class);

	private final Map<WorkspaceId, Resources> resources = new HashMap<WorkspaceId, Resources>();

	public Resources resources(final File scopedResources,
			final Resources global, final FiddleDBIFactory factory,
			final WorkspaceId workspaceId) {

		if (!resources.containsKey(workspaceId)) {

			final ConfigurationFactory<ResourceConfiguration> configurationFactory = ConfigurationFactory
					.forClass(ResourceConfiguration.class, new Validator());

			try {
				resources.put(workspaceId, new Resources(global, factory,
						configurationFactory.build(scopedResources)));
			} catch (Exception e) {
				LOG.error("failed to load workspace resource configuration", e);
				return Resources.EMPTY;
			}
		}
		return resources.get(workspaceId);

	}

}
