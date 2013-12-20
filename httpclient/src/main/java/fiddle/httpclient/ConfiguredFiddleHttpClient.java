package fiddle.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class ConfiguredFiddleHttpClient {

	private final HttpClient client;
	private final String url;

	private final Optional<Map<String, String>> headers;

	public ConfiguredFiddleHttpClient(final HttpClient client, final String url) {
		this(client, url, Optional.<Map<String, String>> absent());
	}

	public ConfiguredFiddleHttpClient(final HttpClient client,
			final String url, Optional<Map<String, String>> headers) {
		this.client = client;
		this.url = url;
		this.headers = headers;
	}

	public ConfiguredFiddleHttpClient withHeader(final String type,
			final String value) {
		final Map<String, String> headers = new HashMap<String, String>(
				this.headers.or(Maps.<String, String> newHashMap()));
		headers.put(type, value);

		return new ConfiguredFiddleHttpClient(client, url, Optional.of(headers));
	}

	public ConfiguredFiddleHttpClient withHeaders(
			final Map<String, String> headers) {

		final Map<String, String> newHeaders = new HashMap<String, String>(
				this.headers.or(Maps.<String, String> newHashMap()));
		newHeaders.putAll(headers);
		
		return new ConfiguredFiddleHttpClient(client, url,
				Optional.fromNullable(newHeaders));
	}

	private void fillInHeadersIfPresent(final HttpRequestBase request) {
		if (headers.isPresent()) {
			for (final Entry<String, String> entry : headers.get().entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}

	}

	public FiddleHttpResponse get() {
		final HttpGet get = new HttpGet(url);

		fillInHeadersIfPresent(get);
		
		try {
			final HttpResponse response = client.execute(get);
			return new FiddleHttpResponse(response);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public FiddleHttpResponse post(final String body) {
		return this.post(body.getBytes());
	}

	public FiddleHttpResponse post(final byte[] body) {
		final HttpPost post = new HttpPost(url);

		fillInHeadersIfPresent(post);
		
		post.setEntity(new EntityTemplate(new ContentProducer() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				os.write(body);
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
	
}
