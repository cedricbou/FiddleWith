package fiddle.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.Mustache;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import fiddle.httpclient.TemplateMapBuilder.MustacheMapTemplateMapBuilder;
import fiddle.httpclient.TemplateStringBuilder.SimpleTemplateStringBuilder;

public class ConfiguredFiddleHttpClient {

	private final HttpClient client;
	private final TemplateStringBuilder url;

	private final TemplateStringBuilder body;

	private final Optional<TemplateMapBuilder> headers;

	private final Logger LOG = LoggerFactory
			.getLogger(ConfiguredFiddleHttpClient.class);

	protected ConfiguredFiddleHttpClient(final HttpClient client,
			final String url) {
		this(client, new SimpleTemplateStringBuilder(url), Optional
				.<TemplateMapBuilder> absent(), TemplateStringBuilder.EMPTY);
	}

	protected ConfiguredFiddleHttpClient(final HttpClient client,
			final TemplateStringBuilder url,
			Optional<TemplateMapBuilder> headers,
			final TemplateStringBuilder body) {
		this.client = client;
		this.url = url;
		this.headers = headers;
		this.body = body;
	}

	public ConfiguredFiddleHttpClient withHeader(final String type,
			final String value) {

		return new ConfiguredFiddleHttpClient(client, url, Optional.of(headers
				.transform(
						new Function<TemplateMapBuilder, TemplateMapBuilder>() {
							@Override
							public TemplateMapBuilder apply(
									TemplateMapBuilder headers) {
								return headers.with(type,
										new SimpleTemplateStringBuilder(value)
												.template());
							}
						}).or(
						new MustacheMapTemplateMapBuilder(ImmutableMap.of(type,
								new SimpleTemplateStringBuilder(value)
										.template())))), body);
	}

	public ConfiguredFiddleHttpClient withHeaders(
			final Map<String, String> headers) {

		return new ConfiguredFiddleHttpClient(client, url,
				Optional.of(this.headers.transform(
						new Function<TemplateMapBuilder, TemplateMapBuilder>() {
							@Override
							public TemplateMapBuilder apply(
									TemplateMapBuilder formerHeaders) {
								return formerHeaders.with(Maps.transformValues(
										headers,
										new Function<String, Mustache>() {
											@Override
											public Mustache apply(String value) {
												return new SimpleTemplateStringBuilder(
														value).template();
											}
										}));
							}
						}).or(
						new MustacheMapTemplateMapBuilder(Maps.transformValues(
								headers, new Function<String, Mustache>() {
									@Override
									public Mustache apply(
											final String templateValue) {
										return new SimpleTemplateStringBuilder(
												templateValue).template();
									}
								})))), body);
	}

	public ConfiguredFiddleHttpClient withBody(final String body) {
		return new ConfiguredFiddleHttpClient(client, url, headers,
				new TemplateStringBuilder.SimpleTemplateStringBuilder(body));
	}

	public ConfiguredFiddleHttpClient withBody(final TemplateStringBuilder body) {
		return new ConfiguredFiddleHttpClient(client, url, headers, body);
	}

	private void fillInHeadersIfPresent(final HttpRequestBase request,
			final Object templateValues) {
		if (headers.isPresent()) {
			for (final Entry<String, String> entry : headers.get()
					.build(templateValues).entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}

	}

	public FiddleHttpResponse get() {
		return get(ImmutableMap.of());
	}

	public FiddleHttpResponse get(final Object templateValues) {
		final HttpGet get = new HttpGet(url.build(templateValues));

		fillInHeadersIfPresent(get, templateValues);

		try {
			final HttpResponse response = client.execute(get);
			return new FiddleHttpResponse(response);
		} catch (IOException ioe) {
			LOG.warn("Failed during get request", ioe);
			return new FiddleHttpResponse(ExceptionHttpResponseBuilder.failed(
					ioe, null));
		}
	}

	public FiddleHttpResponse post(final String body) {
		return this.post(body, ImmutableMap.of());
	}

	public FiddleHttpResponse post(final String body,
			final Object templateValues) {
		return this.post(new SimpleTemplateStringBuilder(body), templateValues);
	}

	public FiddleHttpResponse post(final TemplateStringBuilder bodyBuilder,
			final Object templateValues) {
		final HttpPost post = new HttpPost(url.build(templateValues));

		fillInHeadersIfPresent(post, templateValues);

		post.setEntity(new EntityTemplate(new ContentProducer() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				os.write(bodyBuilder.build(templateValues).getBytes(bodyBuilder.encoding()));
				os.close();
			}
		}));

		try {
			final HttpResponse response = client.execute(post);
			return new FiddleHttpResponse(response);
		} catch (IOException ioe) {
			LOG.warn("Failed during post request", ioe);
			return new FiddleHttpResponse(ExceptionHttpResponseBuilder.failed(
					ioe, null));
		}
	}

	public FiddleHttpResponse post(final Object values) {
		return post(body, values);
	}

	public FiddleHttpResponse post() {
		return post(body, ImmutableMap.of());
	}

}
