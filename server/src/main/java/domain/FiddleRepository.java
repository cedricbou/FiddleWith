package domain;

import domain.ruby.JRubyFiddle;

public class FiddleRepository {

	public Fiddle find(String id) {
		return new JRubyFiddle();
	}

}
