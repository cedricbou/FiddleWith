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

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;

import fiddle.httpclient.FiddleHttpClient.Redirect;
import fiddle.httpclient.FiddleHttpClient.SSL;

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
		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/somehtml"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/somehtml")
				.withHeader("X-First", "first")
				.withHeaders(
						ImmutableMap.<String, String> of("Accept", "text/html",
								"X-Test1", "test1"))
				.withHeader("X-Test2", "test2");

		final FiddleHttpResponse response = client.get();

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(getRequestedFor(urlMatching("/somehtml"))
				.withHeader("Accept", equalTo("text/html"))
				.withHeader("X-Test2", equalTo("test2"))
				.withHeader("X-Test1", equalTo("test1"))
				.withHeader("X-First", equalTo("first")));
	}

	@Test
	public void postWithHeaders() {
		final String html = "<html><body><p>Hello World!</p></body></html>";
		final String post = "hello world";

		stubFor(post(urlEqualTo("/somehtml"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/somehtml")
				.withHeader("X-First", "first")
				.withHeaders(
						ImmutableMap.<String, String> of("Accept", "text/html",
								"X-Test1", "test1"))
				.withHeader("X-Test2", "test2")
				.withHeader("Content-Type", "text/plain");

		final FiddleHttpResponse response = client.post(post);

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(postRequestedFor(urlMatching("/somehtml"))
				.withRequestBody(equalTo(post))
				.withHeader("Accept", equalTo("text/html"))
				.withHeader("X-Test2", equalTo("test2"))
				.withHeader("X-Test1", equalTo("test1"))
				.withHeader("X-First", equalTo("first"))
				.withHeader("Content-Type", equalTo("text/plain")));
	}

	@Test
	public void getNotFound() {
		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/somehtml"))
				.willReturn(
						aResponse().withStatus(404)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/somehtml");

		final FiddleHttpResponse response = client.get();

		assertTrue(response.is4XX());

		verify(getRequestedFor(urlMatching("/somehtml")));

	}

	@Test
	public void getRedirected() {
		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/oldpage"))
				.inScenario("Redirection")
				.whenScenarioStateIs(Scenario.STARTED)
				.willReturn(
						aResponse().withStatus(302).withHeader("location",
								"/newpage")).willSetStateTo("redirected"));

		stubFor(get(urlEqualTo("/newpage"))
				.inScenario("Redirection")
				.whenScenarioStateIs("redirected")
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(client())
				.withUrl("https://localhost:8043/oldpage");
		final FiddleHttpResponse response = client.get();

		assertTrue(response.is2XX());
		assertEquals(html, response.body());

		verify(getRequestedFor(urlMatching("/oldpage")));
		verify(getRequestedFor(urlMatching("/newpage")));
	}

	@Test
	public void getNotRedirected() {
		stubFor(get(urlEqualTo("/oldpage")).willReturn(
				aResponse().withStatus(302).withHeader("location", "/newpage")));

		final ConfiguredFiddleHttpClient client = new FiddleHttpClient(
				client(), Redirect.NONE, SSL.UNCHECKED)
				.withUrl("https://localhost:8043/oldpage");
		final FiddleHttpResponse response = client.get();

		assertTrue(response.is3XX());
		assertEquals("/newpage", response.firstHeader("Location").get());

		verify(getRequestedFor(urlMatching("/oldpage")));
	}
}
