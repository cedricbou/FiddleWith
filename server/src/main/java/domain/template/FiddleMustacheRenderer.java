package domain.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.FallbackMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import domain.TemplateId;
import domain.WorkspaceId;

public class FiddleMustacheRenderer implements FiddleTemplateRenderer {

	private final LoadingCache<WorkspaceId, MustacheFactory> factories;

	public FiddleMustacheRenderer(final File fiddleRepository) {
		this.factories = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build(
				new CacheLoader<WorkspaceId, MustacheFactory>() {
					@Override
					public MustacheFactory load(WorkspaceId id)
							throws Exception {
						final File common = new File(fiddleRepository, WorkspaceId.COMMON.toString());
						
						if(common.exists() && common.canRead()) {
							return new FallbackMustacheFactory(new File(
								fiddleRepository, id.toString()), common);
						}
						else {
							return new DefaultMustacheFactory(new File(
								fiddleRepository, id.toString()));
						}
					}
				});
	}

	@Override
	public void render(final Object entity, final WorkspaceId workspaceId,
			final TemplateId templateId, Locale locale, OutputStream output)
			throws IOException {
		try {
			final Mustache template = factories.get(workspaceId).compile(
					templateId + ".mustache");

			final Charset charset = Charsets.UTF_8;

			OutputStreamWriter writer = new OutputStreamWriter(output, charset);
			template.execute(writer, entity);
			writer.close();

		} catch (Exception e) {
			throw new FileNotFoundException("Template " + templateId + "("
					+ workspaceId + ") not found !(" + e.getMessage() + ")");
		}
	}
	
	@Override
	public void clearCaches() {
		factories.invalidateAll();
		factories.cleanUp();
	}
}
