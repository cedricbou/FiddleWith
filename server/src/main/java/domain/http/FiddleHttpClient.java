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

import com.google.common.base.Optional;

import domain.template.FiddleTemplateRenderer;
import fiddle.api.TemplateId;
import fiddle.api.WorkspaceId;
import fiddle.utils.UriManipulator;

/**
 * Wrapper around an apache http client, it provides request based on templates.
 * 
 * @author Cedric
 * 
 */
public class FiddleHttpClient {

	private final FiddleTemplateRenderer templates;
	private final DefaultHttpClient client;

	public FiddleHttpClient(final FiddleTemplateRenderer renderer,
			final DefaultHttpClient httpClient) {
		this.templates = renderer;
		this.client = httpClient;
		client.setRedirectStrategy(new LaxRedirectStrategy());
	}

	/**
	 * Send a post request to the specified uri, templates are looked inside the
	 * provided workspace id (fallback to default) and rendered using the entity
	 * object.
	 * 
	 * @param uri
	 * @param workspaceId
	 * @param templateId
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public FiddleHttpResponse post(final URI uri, final Optional<Map<String, Object>> queryString,
			final WorkspaceId workspaceId, final Optional<TemplateId> templateId,
			final Optional<Object> entity) throws IOException {
		
		if(templateId.isPresent() && entity.isPresent()) {
			return post(uri, queryString,
				headerFromTemplate(workspaceId, templateId.get(), entity.get()),
				templates.render(entity.get(), workspaceId, templateId.get(),
						Locale.getDefault()));
		}
		else if(templateId.isPresent() && !entity.isPresent()) {
			return post(uri, queryString,
					headerFromTemplate(workspaceId, templateId.get(), Collections.<String, String> emptyMap()),
					templates.render(Collections.<String, String> emptyMap(), workspaceId, templateId.get(),
							Locale.getDefault()));
		}
		else {
			return post(uri, queryString);
		}
	}

	public FiddleHttpResponse post(final URI uri, final Optional<Map<String, Object>> queryString)
			throws IOException {
		return post(
				uri,
				queryString,
				Collections.<String, String> emptyMap(), "");
	}
	
	/**
	 * Send a get request to the uri, it uses only the header template.
	 * @param uri
	 * @param workspaceId
	 * @param templateId
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public FiddleHttpResponse get(final URI uri, final Optional<Map<String, Object>> queryString,
			final WorkspaceId workspaceId,
			final Optional<TemplateId> templateId, final Optional<Object> entity)
			throws IOException {
		if(templateId.isPresent() && entity.isPresent()) {
			return get(uri, queryString,
				headerFromTemplate(workspaceId, templateId.get(), entity.get()));
		}
		else if(templateId.isPresent() && !entity.isPresent()) {
			return get(uri, queryString,
					headerFromTemplate(workspaceId, templateId.get(), Collections.<String, String> emptyMap()));			
		}
		else {
			return get(uri, queryString);
		}
	}

	public FiddleHttpResponse get(final URI uri, final Optional<Map<String, Object>> queryString) throws IOException {
		return get(
				uri,
				queryString,
				Collections.<String, String> emptyMap());
	}
	
	private FiddleHttpResponse get(final URI uri,
			final Optional<Map<String, Object>> queryString,
			final Map<String, String> headers) {

		final HttpGet get = new HttpGet(useQueryString(uri, queryString));

		for (final Entry<String, String> entry : headers.entrySet()) {
			get.setHeader(entry.getKey(), entry.getValue());
		}

		try {
			final HttpResponse response = client.execute(get);
			return new FiddleHttpResponse(response);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private FiddleHttpResponse post(final URI uri,
			final Optional<Map<String, Object>> queryString,
			final Map<String, String> headers, final String data) {
		final HttpPost post = new HttpPost(useQueryString(uri, queryString));

		for (final Entry<String, String> entry : headers.entrySet()) {
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
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private static URI useQueryString(final URI uri,
			final Optional<Map<String, Object>> queryString) {
		final UriManipulator uriManipulator;

		if (queryString.isPresent()) {
			uriManipulator = new UriManipulator(uri)
					.enhanceQueryString(queryString.get());
		} else {
			uriManipulator = new UriManipulator(uri);
		}

		return uriManipulator.uri;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> headerFromTemplate(
			final WorkspaceId workspaceId, final TemplateId templateId,
			final Object entity) {
		final TemplateId headerTpl = new TemplateId(templateId.toString()
				+ ".headers");

		try {
			final String headersContent = templates.render(entity, workspaceId,
					headerTpl, Locale.getDefault());

			final Map<String, String> map = new HashMap<String, String>();

			for (final String header : headersContent.split("[\\r\\n]+")) {
				final int pos = header.indexOf(":");
				if (pos >= 0) {
					map.put(header.substring(0, pos).trim(),
							header.substring(pos + 1).trim());
				}
			}

			return map;
		} catch (IOException ioe) {
			return Collections.EMPTY_MAP;
		}
	}

}
