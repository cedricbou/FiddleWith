package fiddle.httpclient;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.Mustache;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

public interface TemplateMapBuilder {

	public Map<String, String> build(final Object values);

	public TemplateMapBuilder with(final String key,
			final Mustache templateValue);

	public TemplateMapBuilder with(final Map<String, Mustache> entries);

	public static class MustacheTemplateMapBuilder implements
			TemplateMapBuilder {
		private final static Pattern END_OF_LINE = Pattern.compile("\r?\n");

		private final static Logger LOG = LoggerFactory
				.getLogger(MustacheTemplateMapBuilder.class);

		private final Mustache template;

		private final Map<String, Mustache> extras;

		public MustacheTemplateMapBuilder(final Mustache template) {
			this(template, ImmutableMap.<String, Mustache> of());
		}

		public MustacheTemplateMapBuilder(final Mustache template,
				final Map<String, Mustache> extras) {
			this.template = template;
			this.extras = extras;
		}

		@Override
		public Map<String, String> build(final Object values) {
			try (final StringWriter writer = new StringWriter()) {
				template.execute(writer, values);
				return ImmutableMap
						.<String, String> builder()
						.putAll(Splitter.on(END_OF_LINE).trimResults()
								.withKeyValueSeparator(":")
								.split(writer.toString()))
						.putAll(Maps.transformValues(extras,
								new Function<Mustache, String>() {
									@Override
									@Nullable
									public String apply(
											@Nullable Mustache template) {
										try (final StringWriter innerWriter = new StringWriter()) {
											template.execute(innerWriter,
													values);
											return writer.toString();
										} catch (Exception e) {
											LOG.error(
													"Failed to execute mustache template",
													e);
											return "";
										}
									}
								})).build();
			} catch (Exception e) {
				LOG.error("Failed to execute mustache template", e);
				return ImmutableMap.<String, String> of();
			}
		}

		@Override
		public TemplateMapBuilder with(String key, Mustache templateValue) {
			return new MustacheTemplateMapBuilder(template, ImmutableMap
					.<String, Mustache> builder().putAll(extras)
					.put(key, templateValue).build());
		}

		@Override
		public TemplateMapBuilder with(Map<String, Mustache> entries) {
			return new MustacheTemplateMapBuilder(template, ImmutableMap
					.<String, Mustache> builder().putAll(extras)
					.putAll(entries).build());
		}
	}

	public static class MustacheMapTemplateMapBuilder implements
			TemplateMapBuilder {
		private final static Logger LOG = LoggerFactory
				.getLogger(MustacheMapTemplateMapBuilder.class);

		private final Map<String, Mustache> templates;

		public MustacheMapTemplateMapBuilder(
				final Map<String, Mustache> templates) {
			this.templates = templates;
		}

		@Override
		public Map<String, String> build(final Object values) {
			return Maps.transformEntries(templates,
					new EntryTransformer<String, Mustache, String>() {
						@Override
						public String transformEntry(@Nullable String key,
								@Nullable Mustache template) {
							try (final StringWriter writer = new StringWriter()) {
								template.execute(writer, values);
								return writer.toString();
							} catch (Exception e) {
								LOG.error(
										"Failed to execute mustache template",
										e);
								return "";
							}
						}
					});
		}

		@Override
		public TemplateMapBuilder with(String key, Mustache templateValue) {
			return new MustacheMapTemplateMapBuilder(ImmutableMap
					.<String, Mustache> builder().putAll(templates)
					.put(key, templateValue).build());
		}

		@Override
		public TemplateMapBuilder with(Map<String, Mustache> entries) {
			return new MustacheMapTemplateMapBuilder(ImmutableMap
					.<String, Mustache> builder().putAll(templates)
					.putAll(entries).build());
		}
	}

}
