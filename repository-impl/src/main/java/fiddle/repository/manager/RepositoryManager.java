package fiddle.repository.manager;

import java.io.File;

import com.github.mustachejava.Mustache;
import com.yammer.dropwizard.config.Environment;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.config.ResourceFileName;
import fiddle.repository.Repository;
import fiddle.resources.Resources;
import fiddle.resources.builder.DropwizardBuilders;

public class RepositoryManager {

	private final FiddlesRepositoryManager fiddles;
	private final TemplatesRepositoryManager templates;
	private final ResourcesRepositoryManager resources;
	
	public RepositoryManager(final FiddlesRepositoryManager fiddles, final TemplatesRepositoryManager templates, final ResourcesRepositoryManager resources) {
		this.fiddles = fiddles;
		this.templates = templates;
		this.resources = resources;
	}
	
	public RepositoryManager(final File repoFolder, final Environment environment) {
		this.fiddles = new FiddlesRepositoryManager(repoFolder);
		this.templates = new TemplatesRepositoryManager(repoFolder);
		final DropwizardBuilders builders = new DropwizardBuilders(environment, templates);
		this.resources = new ResourcesRepositoryManager(repoFolder, builders.dbiBuilder(), builders.httpBuilder());
	}

	public final Repository<Fiddle, Fiddle, FiddleId> fiddles(
			final WorkspaceId id) {
		return fiddles.repo(id);
	}

	public final Repository<Mustache, String, TemplateId> templates(
			final WorkspaceId id) {
		return templates.repo(id);
	}

	public final Repository<Resources, String, ResourceFileName> resources(
			final WorkspaceId id) {
		return resources.repo(id);
	}

	public FiddlesRepositoryManager fiddlesManager() {
		return fiddles;
	}
	
	public TemplatesRepositoryManager templatesManager() {
		return templates;
	}
	
	public ResourcesRepositoryManager resourcesManager() {
		return resources;
	}
	
}
