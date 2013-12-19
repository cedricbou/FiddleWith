package fiddle.httpclient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;

public class HowToUse {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089, 8043);

	private static DefaultHttpClient client() {
		return (DefaultHttpClient) new HttpClientBuilder().using(
				new HttpClientConfiguration()).build();
	}

	@Test
	public void basicGet() {
		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/somehtml"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("http://localhost:8089/somehtml");

		final FiddleHttpResponse response = client.get();

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(getRequestedFor(urlMatching("/somehtml")));
	}

	@Test
	public void basicHttpsGet() {
		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/somehtml"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/somehtml");

		final FiddleHttpResponse response = client.get();

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(getRequestedFor(urlMatching("/somehtml")));
	}

	@Test
	public void basicPost() {
		final String html = "<html><body><p>Hello World!</p></body></html>";
		final String json = "{\"foo\":56, \"bar\":\"azerty\"}";

		stubFor(post(urlEqualTo("/postjson"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("http://localhost:8089/postjson");

		final FiddleHttpResponse response = client.post(json);

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(postRequestedFor(urlMatching("/postjson")).withRequestBody(
				equalTo(json)));
	}

	@Test
	public void basicHttpsPost() {
		final String html = "<html><body><p>Hello World!</p></body></html>";
		final String json = "{\"foo\":56, \"bar\":\"azerty\"}";

		stubFor(post(urlEqualTo("/postjson"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/postjson");

		final FiddleHttpResponse response = client.post(json);

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(postRequestedFor(urlMatching("/postjson")).withRequestBody(
				equalTo(json)));
	}
	
	@Test
	public void getWithHeaders() {
		fail("not implemented");
	}
	
}
