package fiddle.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;

public class TemplateFileEntityManager implements
		FileEntityManager<TemplateId, String> {

	private static final Logger LOG = LoggerFactory
			.getLogger(TemplateFileEntityManager.class);

	private final File dir;
	private final WorkspaceId workspaceId;

	public TemplateFileEntityManager(final File dir,
			final WorkspaceId workspaceId) {
		this.dir = new File(dir, workspaceId.id);
		this.workspaceId = workspaceId;
	}

	@Override
	public Optional<String> find(TemplateId id) {
		final File tpl = new File(dir, id + ".mustache");

		if (tpl.exists() && tpl.isFile() && tpl.canRead()) {
			try (final FileInputStream in = new FileInputStream(tpl)) {
				return Optional.of(new String(ByteStreams.toByteArray(in)));
			} catch (IOException ioe) {
				LOG.warn(
						"failed to read template " + workspaceId.id + "/" + id,
						ioe);
				return Optional.absent();
			}
		}

		return Optional.absent();
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
