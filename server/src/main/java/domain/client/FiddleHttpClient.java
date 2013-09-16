package domain.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;

import com.google.common.net.MediaType;

import domain.TemplateId;
import domain.WorkspaceId;
import domain.template.FiddleTemplateRenderer;

public class FiddleHttpClient {

	private final FiddleTemplateRenderer templates;
	private final HttpClient client;
	
	public FiddleHttpClient(final FiddleTemplateRenderer renderer, final HttpClient httpClient) {
		this.templates = renderer;
		this.client = httpClient;
	}
	
	public FiddleHttpResponse post(final URI uri, final MediaType type, final TemplateId templateId, final Object vars) {
		final HttpPost post = new HttpPost(uri);
		
		post.setHeader("Content-Type", type.toString());
		post.setEntity(new EntityTemplate(new ContentProducer() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				templates.render(vars, WorkspaceId.COMMON, templateId, Locale.getDefault(), os);
			}
		}));
		
		try {
			final HttpResponse response = client.execute(post);
			return new FiddleHttpResponse(response);
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}
