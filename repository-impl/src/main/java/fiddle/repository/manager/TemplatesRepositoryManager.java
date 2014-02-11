package fiddle.repository.manager;

import java.io.File;

import com.github.mustachejava.Mustache;

import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.repository.GuavaCachedRepository;
import fiddle.repository.Repository;
import fiddle.repository.RepositoryManager;
import fiddle.repository.RepositoryResourceLoader;
import fiddle.repository.impl.TemplateRepository;

public class TemplatesRepositoryManager  implements RepositoryManager<Repository<Mustache, String, TemplateId>> {

	private final File repoFolder;

	public TemplatesRepositoryManager(final File repoFolder) {
		this.repoFolder = repoFolder;
	}

	public final Repository<Mustache, String, TemplateId> repo(
			final WorkspaceId id) {
		final Repository<Mustache, String, TemplateId> combined = new TemplateRepository(
				new File(repoFolder, id.id), new File(repoFolder,
						WorkspaceId.COMMON.id));

		return new GuavaCachedRepository<Mustache, String, TemplateId>(
				combined,
				new RepositoryResourceLoader<Mustache, String, TemplateId>(
						combined), 50, 5);
	}
}
