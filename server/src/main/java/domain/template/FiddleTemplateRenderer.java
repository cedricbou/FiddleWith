package domain.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import domain.TemplateId;
import domain.WorkspaceId;

public interface FiddleTemplateRenderer {

	public void render(final Object entity, final WorkspaceId workspaceId,
			final TemplateId templateId, Locale locale, OutputStream output)
			throws IOException;
}