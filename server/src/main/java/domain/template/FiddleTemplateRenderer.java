package domain.template;

import java.io.IOException;
import java.util.Locale;

import domain.TemplateId;
import domain.WorkspaceId;

public interface FiddleTemplateRenderer {

	public String render(final Object entity, final WorkspaceId workspaceId,
			final TemplateId templateId, Locale locale)
			throws IOException;
	
	public void clearCaches();
}