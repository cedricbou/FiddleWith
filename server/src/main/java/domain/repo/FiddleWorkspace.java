package domain.repo;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import domain.Fiddle;
import domain.FiddleId;
import domain.ruby.JRubyFiddle;
import domain.ruby.RubyEnv;
import domain.ruby.RubyScript;

public class FiddleWorkspace {
	private final RubyEnv env;

	private final File workspaceRepository;

	protected FiddleWorkspace(final File workspaceRepository, final RubyEnv env) {
		this.env = env;
		this.workspaceRepository = workspaceRepository;
	}

	
	public Optional<? extends Fiddle> find(final FiddleId id) {
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

	public Fiddle replace(final FiddleId id, final Fiddle fiddle)
			throws IOException {
		final File to = buildFile(id, fiddle.getLanguage());
		Files.write(fiddle.getScript().getBytes(), to);
		return fiddle;
	}

	private Optional<File> findFile(final FiddleId id) {
		for (final Language lang : Language.values()) {
			final File candidate = new File(workspaceRepository, id + "." + lang.suffix);
			if (candidate.exists() && candidate.canRead()) {
				return Optional.of(candidate);
			}
		}
		return Optional.absent();
	}

	private File buildFile(final FiddleId id, final Language lang) {
		return new File(workspaceRepository, id + "." + lang.suffix);
	}
	
}
