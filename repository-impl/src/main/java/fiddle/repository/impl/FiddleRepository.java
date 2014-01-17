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
import fiddle.repository.Repository;

public class FiddleRepository implements Repository<Fiddle, Fiddle, FiddleId> {

	private final File repo;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public FiddleRepository(final File repo) {
		this.repo = repo;
	}

	@Override
	public Optional<Fiddle> open(FiddleId id) {
		for (final Language lang : Language.values()) {
			final File fiddle = new File(repo, id.id + "." + lang.suffix);

			if (fiddle.exists() && fiddle.isFile() && fiddle.canRead()) {
				try (final FileInputStream in = new FileInputStream(fiddle)) {
					return Optional.of(new Fiddle(new String(ByteStreams
							.toByteArray(in)), lang));
				} catch (IOException ioe) {
					LOG.warn(
							"failed to read fiddle "
									+ fiddle.getParentFile().getName() + "/"
									+ fiddle.getName(), ioe);
					return Optional.absent();
				}
			}
		}

		return Optional.absent();
	}

	public void write(FiddleId id, Fiddle rsc) throws java.io.IOException {
		final File outFile = new File(repo, id.id + "." + rsc.language.suffix);

		try {
			Files.write(rsc.content.getBytes(), outFile);
		} catch (IOException e) {
			final String fiddlePath = outFile.getParentFile().getName() + "/"
					+ outFile.getName();
			LOG.error("Failed to write fiddle " + fiddlePath);
			throw new IOException("file " + fiddlePath + " is not writeable", e);
		}
	}

}
