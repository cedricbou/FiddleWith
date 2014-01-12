package fiddle.ruby.api;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.google.common.base.Optional;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.client.HttpClientConfiguration;

import fiddle.api.Fiddle;
import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;
import fiddle.config.Resources;
import fiddle.httpclient.FiddleHttpClient;
import fiddle.httpclient.registry.HttpRegistry;
import fiddle.repository.Repository;
import fiddle.ruby.RubyExecutor;

public class HttpClientTest {

	private final Repository repo;

	private final static WorkspaceId WS = new WorkspaceId("http");

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private final static RubyExecutor ex = new RubyExecutor();

	private final Resources resources = mock(Resources.class);

	private final HttpRegistry https = mock(HttpRegistry.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089, 8043);

	public HttpClientTest() {
		URL url = this.getClass().getResource("localised.donoterase");

		File repoDir = new File(new File(url.getFile()).getParentFile(),
				"repository");

		when(resources.defaultHttp()).thenReturn(
				new FiddleHttpClient(new HttpClientBuilder().using(
						new HttpClientConfiguration()).build()));

		repo = new Repository(repoDir);
	}

	@Test
	public void testSimpleGet() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("simpleget");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals(html, r.getEntity().toString());

		verify(getRequestedFor(urlMatching("/greets")));
	}

	@Test
	public void testRedirectedGet() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("redirectedget");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/greets"))
				.inScenario("Redirection")
				.whenScenarioStateIs(Scenario.STARTED)
				.willReturn(
						aResponse().withStatus(302).withHeader("location",
								"https://localhost:8043/aloha"))
				.willSetStateTo("redirected"));

		stubFor(get(urlEqualTo("/aloha"))
				.inScenario("Redirection")
				.whenScenarioStateIs("redirected")
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals(html, r.getEntity().toString());

		verify(getRequestedFor(urlMatching("/greets")));
		verify(getRequestedFor(urlMatching("/aloha")));
	}

	@Test
	public void testSimpleGetWithHeaders() throws JsonProcessingException,
			IOException {
		final FiddleId id = new FiddleId("simplegetwithheaders");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(get(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals(html, r.getEntity().toString());

		verify(getRequestedFor(urlMatching("/greets")).withHeader("accept",
				equalTo("foo/bar")));
	}

	@Test
	public void testSimplePost() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("simplepost");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(post(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals(html, r.getEntity().toString());

		verify(postRequestedFor(urlMatching("/greets")).withRequestBody(
				equalTo("{\"foo\":\"bar\"}")));
	}

	@Test
	public void testSimplePostWithHeaders() throws JsonProcessingException,
			IOException {
		final FiddleId id = new FiddleId("simplepostwithheaders");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(post(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/html")
								.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(200, r.getStatus());
		assertEquals(html, r.getEntity().toString());

		verify(postRequestedFor(urlMatching("/greets")).withRequestBody(
				equalTo("{\"foo\":\"bar\"}")).withHeader("foo", equalTo("bar")));
	}
	
	@Test
	public void testError500Get() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("error500get");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		stubFor(get(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(500, r.getStatus());
		assertEquals("The server failed to respond with a valid HTTP response", r.getEntity().toString());

		verify(getRequestedFor(urlMatching("/greets")));
	}

	@Test
	public void testTimeoutPost() throws JsonProcessingException, IOException {
		final FiddleId id = new FiddleId("timeoutpost");
		final Optional<Fiddle> f = repo.fiddles(WS).open(id);

		final String html = "<html><body><p>Hello World!</p></body></html>";

		stubFor(post(urlEqualTo("/greets"))
				.willReturn(
						aResponse().withFixedDelay(600)
						.withHeader("content", "text/html")
						.withBody(html)));

		final Response r = ex.execute(resources, id, f.get(),
				MAPPER.readTree("{}"));

		assertEquals(500, r.getStatus());
		assertEquals("Read timed out", r.getEntity().toString());

		verify(postRequestedFor(urlMatching("/greets")));
	}

	
}
