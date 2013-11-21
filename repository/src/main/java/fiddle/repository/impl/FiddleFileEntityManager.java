package fiddle.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.WorkspaceId;

public class FiddleFileEntityManager implements FileEntityManager<FiddleId, Fiddle>{
	
	private static final Logger LOG = LoggerFactory
			.getLogger(FiddleFileEntityManager.class);

	private final File dir;
	private final WorkspaceId workspaceId;

	
	public FiddleFileEntityManager(final File dir, final WorkspaceId workspaceId) {
		this.dir = new File(dir, workspaceId.id);
		this.workspaceId = workspaceId;
	}
	
	@Override
	public Optional<Fiddle> find(FiddleId fiddleId) {
		for (final Language lang : Language.values()) {
			final File fiddle = new File(dir, fiddleId.id + "." + lang.suffix);

			if (fiddle.exists() && fiddle.isFile() && fiddle.canRead()) {
				try (final FileInputStream in = new FileInputStream(fiddle)) {
					return Optional.of(new Fiddle(new String(ByteStreams
							.toByteArray(in)), lang));
				} catch (IOException ioe) {
					LOG.warn("failed to read fiddle " + workspaceId.id + "/"
							+ fiddleId.id, ioe);
					return Optional.absent();
				}
			}
		}
		
		return Optional.absent();
	}
	
	@Override
	public void write(FiddleId fiddleId, Fiddle fiddle) throws IOException {
		final File outFile = new File(dir, fiddleId + "."
				+ fiddle.language.suffix);

		try {
			Files.write(fiddle.content.getBytes(), outFile);
		} catch(IOException e) {
			throw new IOException("file " + outFile.getName()
					+ " is not writeable in workspace "
					+ workspaceId.toString(), e);
		}
	}
	
}
