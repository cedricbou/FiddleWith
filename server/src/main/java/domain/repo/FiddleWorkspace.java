package domain.repo;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import domain.ExecutableFiddle;
import domain.FiddleEnvironment;
import domain.ruby.JRubyFiddle;
import domain.ruby.RubyScript;
import fiddle.api.FiddleId;
import fiddle.api.Language;
import fiddle.api.WorkspaceId;

public class FiddleWorkspace {
	private final FiddleEnvironment env;

	private final File workspaceRepository;
	
	private final WorkspaceId workspaceId;

	protected FiddleWorkspace(final WorkspaceId workspaceId, final File workspaceRepository, final FiddleEnvironment env) {
		this.env = env;
		this.workspaceRepository = workspaceRepository;
		this.workspaceId = workspaceId;
	}

	
	public Optional<? extends ExecutableFiddle> find(final FiddleId id) {
		final Optional<File> scriptFile = findFile(id);

		if (scriptFile.isPresent()) {
			try {
				if (Files.getFileExtension(scriptFile.get().getAbsolutePath())
						.equals(Language.RUBY.suffix)) {
					return Optional.of(new JRubyFiddle(workspaceId, env, new RubyScript(
							scriptFile.get())));
				}
			} catch (IOException e) {
				return Optional.absent();
			}
		}

		return Optional.absent();
	}

	public ExecutableFiddle replace(final FiddleId id, final ExecutableFiddle fiddle)
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
