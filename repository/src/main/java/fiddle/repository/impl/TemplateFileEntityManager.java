package fiddle.repository.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.FallbackMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Optional;
import com.google.common.io.Files;

import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;

public class TemplateFileEntityManager implements
		FileEntityManager<TemplateId, Mustache, String> {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(TemplateFileEntityManager.class);

	private final File dir;
	
	private final WorkspaceId workspaceId;
	
	private final MustacheFactory factory;
	
	public TemplateFileEntityManager(final File dir,
			final WorkspaceId workspaceId, final WorkspaceId fallbackWorkspaceId) {
		
		this.dir = new File(dir, workspaceId.id);
		
		this.workspaceId = workspaceId;
		
		final File rDefault = new File(dir, workspaceId.id);
		
		final File rCommon;
		
		if(null != fallbackWorkspaceId && (rCommon = new File(dir, fallbackWorkspaceId.id)).exists()) {
			this.factory = new FallbackMustacheFactory(rDefault, rCommon);
		}
		else {
			this.factory = new DefaultMustacheFactory(rDefault);
		}
	}

	@Override
	public Optional<Mustache> find(TemplateId id) {
		try {
			return Optional.of(factory.compile(id.id + ".mustache"));
		}
		catch(Exception e) {
			LOG.warn("failed to compile mustache", e);
			return Optional.absent();
		}
	}

	@Override
	public void write(TemplateId id, String entity) throws IOException {
		final File outFile = new File(dir, id + ".mustache");

		try {
			Files.write(entity.getBytes(), outFile);
		} catch (IOException e) {
			throw new IOException("file " + outFile.getName()
					+ " is not writeable in workspace "
					+ workspaceId.toString(), e);
		}
	}

}
