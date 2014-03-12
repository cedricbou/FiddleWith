package fiddle.repository.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
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
	/**
	 * Open a fiddle from its id.
	 * 
	 * The fiddle content must be encoded with UTF-8.
	 */
	public Optional<Fiddle> open(FiddleId id) {
		for (final Language lang : Language.values()) {
			final File fiddle = new File(repo, id.id + "." + lang.suffix);

			if (fiddle.exists() && fiddle.isFile() && fiddle.canRead()) {
				try (final FileInputStream in = new FileInputStream(fiddle)) {
					return Optional.of(new Fiddle(new String(ByteStreams
							.toByteArray(in), Charsets.UTF_8), lang));
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

	/**
	 * Write the fiddle content to the corresponding file.
	 * 
	 * The content must be UTF-8 encoded.
	 */
	public void write(FiddleId id, Fiddle rsc) throws java.io.IOException {
		final File outFile = new File(repo, id.id + "." + rsc.language.suffix);

		try {
			Files.write(rsc.content.getBytes(Charsets.UTF_8), outFile);
		} catch (IOException e) {
			final String fiddlePath = outFile.getParentFile().getName() + "/"
					+ outFile.getName();
			LOG.error("Failed to write fiddle " + fiddlePath);
			throw new IOException("file " + fiddlePath + " is not writeable", e);
		}
	}

	public ImmutableList<FiddleId> ids() {
		return ImmutableList.<FiddleId>builder().addAll(Iterators.transform(Iterators.forArray(repo.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile() && pathname.canRead()) {
					for (final Language lang : Language.values()) {
						return lang.suffix.equals(Files
								.getFileExtension(pathname.getName()));
					}
				}
				return false;
			}
		})), new Function<File, FiddleId>() {
			@Override
			public FiddleId apply(final File input) {
				return new FiddleId(Files.getNameWithoutExtension(input.getName()));
			}
		})).build();
	}

}
