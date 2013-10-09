package fiddle.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.WorkspaceId;
import fiddle.repository.ScopedFiddleRepository;

public class PresentScopedFiddleRepository implements ScopedFiddleRepository {

	private static final Logger LOG = LoggerFactory
			.getLogger(PresentScopedFiddleRepository.class);

	private final File dir;
	private final WorkspaceId workspaceId;
	private final ScopedFiddleRepository common;

	public PresentScopedFiddleRepository(final WorkspaceId workspaceId,
			final File dir, final ScopedFiddleRepository common) {
		this.dir = dir;
		this.workspaceId = workspaceId;
		this.common = common;
	}

	@Override
	public Optional<Fiddle> openFiddle(FiddleId fiddleId) {
		for (final Language lang : Language.values()) {
			final File fiddle = new File(dir, fiddleId.id + "." + lang.suffix);

			if (fiddle.exists() && fiddle.isFile() && fiddle.canRead()) {
				try (final FileInputStream in = new FileInputStream(fiddle)) {
					return Optional.of(new Fiddle(new String(ByteStreams.toByteArray(in)), lang));
				} catch (IOException ioe) {
					LOG.warn("failed to read fiddle " + workspaceId.id + "/"
							+ fiddleId.id, ioe);
					return Optional.absent();
				}
			}
		}

		return common.openFiddle(fiddleId);
	}
}
