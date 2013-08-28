package domain;

import domain.ruby.JRubyFiddle;

public class FiddleRepository {

	private final FiddleEnvironment env;
	
	public FiddleRepository(final FiddleEnvironment env) {
		this.env = env;
	}
	
	public Fiddle find(String id) {
		return new JRubyFiddle(env);
	}

}
