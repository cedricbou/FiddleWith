package fiddle.httpclient;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public interface BodyBuilder {

	public String build(final Object values);

	public static BodyBuilder EMPTY = new SimpleBodyBuilder("");

	public static class MustacheBodyBuilder implements BodyBuilder {
		private final static Logger LOG = LoggerFactory.getLogger(MustacheBodyBuilder.class);
		private final Mustache body;

		public MustacheBodyBuilder(final Mustache body) {
			this.body = body;
		}
		
		@Override
		public String build(Object values) {
			try (final StringWriter writer = new StringWriter()) {
				body.execute(writer, values);
				return writer.toString();
			} catch (Exception e) {
				LOG.error("Failed to execute mustache template", e);
				return "";
			}
		}
	}
	
	public static class SimpleBodyBuilder extends MustacheBodyBuilder {
		private final static MustacheFactory mf = new DefaultMustacheFactory();

		public SimpleBodyBuilder(final String body) {
			super(mf.compile(new StringReader(body), UUID.randomUUID().toString()));
		}
	}
}
