package domain;

import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Resources;

public class RubyScript {

	private final String preBidouille;
	
	private final String customScript;
	
	public RubyScript(final String script) throws IOException {
		this.preBidouille = Resources.toString(Resources.getResource("pre-bidouille.rb"), Charset.forName("UTF-8"));
		
	}
}
