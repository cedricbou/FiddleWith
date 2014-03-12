package fiddle.httpclient;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.base.Charsets;

public interface TemplateStringBuilder {

	public String build(final Object values);
	
	public Charset encoding();

	public static TemplateStringBuilder EMPTY = new SimpleTemplateStringBuilder(
			"");

	public static class MustacheTemplateStringBuilder implements
			TemplateStringBuilder {
		private final static Logger LOG = LoggerFactory
				.getLogger(MustacheTemplateStringBuilder.class);
		private final Mustache template;

		public MustacheTemplateStringBuilder(final Mustache template) {
			this.template = template;
		}

		@Override
		public String build(Object values) {
			try (final StringWriter writer = new StringWriter()) {
				template.execute(writer, values);
				return writer.toString();
			} catch (Exception e) {
				LOG.error("Failed to execute mustache template", e);
				return "";
			}
		}
		
		@Override
		public Charset encoding() {
			return Charsets.UTF_8; // FIXME: find a more elegant way to deal with encoding.
		}
		
		public Mustache template() {
			return template;
		}
	}

	public static class SimpleTemplateStringBuilder extends
			MustacheTemplateStringBuilder {

		public SimpleTemplateStringBuilder(final String body) {
			super(new DefaultMustacheFactory().compile(new StringReader(body),
					UUID.randomUUID().toString()));
		}
	}

	public static class StaticTemplateStringBuilder implements
			TemplateStringBuilder {
		private final String value;

		public StaticTemplateStringBuilder(final String value) {
			this.value = value;
		}

		@Override
		public String build(Object values) {
			return value;
		}
		
		@Override
		public Charset encoding() {
			return Charsets.UTF_8; // FIXME: find a more elegant way to deal with encoding.
		}
	}
}
