package domain.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;

import domain.TemplateId;
import domain.WorkspaceId;
import domain.template.FiddleTemplateRenderer;

public class FiddleHttpClient {

	private final FiddleTemplateRenderer templates;
	private final DefaultHttpClient client;
	
	public FiddleHttpClient(final FiddleTemplateRenderer renderer, final DefaultHttpClient httpClient) {
		this.templates = renderer;
		this.client = httpClient;
		client.setRedirectStrategy(new LaxRedirectStrategy());
	}
		
	public FiddleHttpResponse post(final URI uri, final WorkspaceId workspaceId, final TemplateId templateId, final Object entity) throws IOException {
		return post(uri, headerFromTemplate(workspaceId, templateId, entity), templates.render(entity, workspaceId, templateId, Locale.getDefault()));
	}

	public FiddleHttpResponse post(final URI uri, final WorkspaceId workspaceId, final TemplateId templateId) throws IOException {
		return post(uri, headerFromTemplate(workspaceId, templateId, Collections.EMPTY_MAP), templates.render(Collections.EMPTY_MAP, workspaceId, templateId, Locale.getDefault()));
	}

	public FiddleHttpResponse get(final URI uri, final WorkspaceId workspaceId, final TemplateId templateId, final Object entity) throws IOException {
		return get(uri, headerFromTemplate(workspaceId, templateId, entity));
	}

	public FiddleHttpResponse get(final URI uri, final WorkspaceId workspaceId, final TemplateId templateId) throws IOException {
		return get(uri, headerFromTemplate(workspaceId, templateId, Collections.EMPTY_MAP));
	}

	private FiddleHttpResponse get(final URI uri, final Map<String, String> headers) {
		final HttpGet get = new HttpGet(uri);
		
		for(final Entry<String, String> entry : headers.entrySet()) {
			get.setHeader(entry.getKey(), entry.getValue());
		}

		try {
			final HttpResponse response = client.execute(get);
			return new FiddleHttpResponse(response);
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}		
	}
	
	
	private FiddleHttpResponse post(final URI uri, final Map<String, String> headers, final String data) {
		final HttpPost post = new HttpPost(uri);
		
		for(final Entry<String, String> entry : headers.entrySet()) {
			post.setHeader(entry.getKey(), entry.getValue());
		}
		
		post.setEntity(new EntityTemplate(new ContentProducer() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				os.write(data.getBytes());
				os.close();
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
	
	@SuppressWarnings("unchecked")
	private Map<String, String> headerFromTemplate(final WorkspaceId workspaceId, final TemplateId templateId, final Object entity) {
		final TemplateId headerTpl = new TemplateId(templateId.toString() + ".headers");
		
		try {
			final String headersContent = templates.render(entity, workspaceId, headerTpl, Locale.getDefault());

			final Map<String, String> map = new HashMap<String, String>();

			for(final String header : headersContent.split("[\\r\\n]+")) {
				final int pos = header.indexOf(":");
				if(pos >= 0) {
					map.put(header.substring(0, pos).trim(), header.substring(pos + 1).trim());
				}
			}
			
			return map;
		}
		catch(IOException ioe) {
			return Collections.EMPTY_MAP;
		}
	}

}
