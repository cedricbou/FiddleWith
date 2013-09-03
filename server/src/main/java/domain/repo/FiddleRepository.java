package domain.repo;

import java.io.File;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import domain.Fiddle;
import domain._404Fiddle;
import domain.ruby.JRubyFiddle;
import domain.ruby.RubyEnv;
import domain.ruby.RubyScript;

public class FiddleRepository {

	private final RubyEnv env;
	
	private final File fiddleRepository;
	
	private final static String[] SUFFIXES = new String[] {
		".rb"
	};
		
	public FiddleRepository(final File fiddleRepository, final RubyEnv env) {
		this.env = env;
		this.fiddleRepository = fiddleRepository;
	}
	
	public Fiddle find(final String id) {
		Optional<File> scriptFile = findFile(id);
		if(scriptFile.isPresent()) {
			if(Files.getFileExtension(scriptFile.get().getAbsolutePath()).equals("rb")) {
				return new JRubyFiddle(env, new RubyScript(scriptFile.get()));
			}
		}
		return new _404Fiddle("no such fiddle '" + id + "' (" + scriptFile.toString() + ")");
	}

	private Optional<File> findFile(final String id) {
		for(final String suffix : SUFFIXES) {
			final File candidate = new File(fiddleRepository, id + suffix);
			if(candidate.exists() && candidate.canRead()) {
				return Optional.of(candidate);
			}
		}
		return Optional.absent();
	}
	
}
