package domain.ruby;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;

public class RubyScript {

	public final String script;
	
	public final String userScript;

	public RubyScript(final File file) throws IOException {
		this(Files.toString(file, Charset.forName("UTF-8")));
	}
	
	public RubyScript(final String content) {
		try {
		final String preBidouille = CharStreams.toString(new InputStreamReader(
				Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("pre-fiddle.rb"), "UTF-8"));
		
		this.userScript = content;
		this.script = preBidouille + userScript;
		} catch(IOException e) {
			throw new RuntimeException("failed to load ruby script", e);
		}		
	}
}
