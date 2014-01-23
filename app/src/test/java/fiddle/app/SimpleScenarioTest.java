package fiddle.app;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;

import resources.FiddleResource;
import app.AppAuthenticator;
import app.AppConfiguration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.validation.Validator;

import fiddle.password.PasswordManager;
import fiddle.repository.impl.RepositoryManager;

public class SimpleScenarioTest extends ResourceTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089, 8043);

	@Override
	protected void setUpResources() throws Exception {
		final ConfigurationFactory<AppConfiguration> configurationFactory = ConfigurationFactory
				.forClass(AppConfiguration.class, new Validator());

		final AppConfiguration config = configurationFactory.build(new File(
				"src/test/config/application.conf"));

		final Environment env = new Environment("test", config,
				getObjectMapperFactory(), getValidator());

		final PasswordManager passwords = config.getUsers();

		final RepositoryManager repo = new RepositoryManager(new File(
				config.getRepository()), env);

		addProvider(new BasicAuthProvider<PasswordManager.UserConfiguration>(
				new AppAuthenticator(passwords), "fiddle with!"));
		addResource(new FiddleResource(repo));
	}

	@Test
	public void basicTest() {
		assertThat(
				client().resource("/fiddle/with/basics/checkup?a=3&b=5").get(
						String.class)).isEqualTo("ok : 8");
	}

	@Test
	public void meteoAsXmlTest() {
		final String xml = "<meteo><city name='lille'><temp unit='celsius'>25</temp><wind unit='km/h'>12</wind></city></meteo>";
		final String json = "{\"city\" : { \"name\" : \"lille\", \"temp\" : 25 , \"wind\" : 12 }}";

		stubFor(get(urlEqualTo("/meteo/lille")).withHeader("accept",
				equalTo("text/xml")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/xml").withBody(xml)));

		stubFor(get(urlEqualTo("/meteo/lille")).withHeader("accept",
				equalTo("text/json"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/json")
								.withBody(json)));

		assertThat(
				client().resource("/fiddle/with/basics/meteo?city=lille").get(
						String.class)).isEqualTo("meteo : lille : 25, 12");

		verify(getRequestedFor(urlMatching("/meteo/lille")).withHeader(
				"accept", equalTo("text/xml")));
		verify(getRequestedFor(urlMatching("/meteo/lille")).withHeader(
				"accept", equalTo("text/json")));
	}

}
