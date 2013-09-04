package domain.repo;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import domain.Fiddle;
import domain.ruby.JRubyFiddle;
import domain.ruby.RubyEnv;
import domain.ruby.RubyScript;

public class FiddleRepository {

	private final RubyEnv env;

	private final File fiddleRepository;

	public FiddleRepository(final File fiddleRepository, final RubyEnv env) {
		this.env = env;
		this.fiddleRepository = fiddleRepository;
	}

	public Optional<? extends Fiddle> find(final String id) {
		final Optional<File> scriptFile = findFile(id);

		if (scriptFile.isPresent()) {
			try {
				if (Files.getFileExtension(scriptFile.get().getAbsolutePath())
						.equals(Language.RUBY.suffix)) {
					return Optional.of(new JRubyFiddle(env, new RubyScript(
							scriptFile.get())));
				}
			} catch (IOException e) {
				return Optional.absent();
			}
		}

		return Optional.absent();
	}

	public Fiddle replace(final String id, final Fiddle fiddle)
			throws IOException {
		final File to = buildFile(id, fiddle.getLanguage());
		Files.write(fiddle.getScript().getBytes(), to);
		return fiddle;
	}

	private Optional<File> findFile(final String id) {
		for (final Language lang : Language.values()) {
			final File candidate = new File(fiddleRepository, id + "." + lang.suffix);
			if (candidate.exists() && candidate.canRead()) {
				return Optional.of(candidate);
			}
		}
		return Optional.absent();
	}

	private File buildFile(final String id, final Language lang) {
		return new File(fiddleRepository, id + "." + lang.suffix);
	}

}
