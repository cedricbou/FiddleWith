package fiddle.config;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.yammer.dropwizard.config.ConfigurationException;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

import fiddle.config.ResourceConfiguration;


public class ReadConfigTest {
	
	@Test
	public void readConfigurationFileTest() throws IOException, ConfigurationException {
		final ConfigurationFactory<ResourceConfiguration> configurationFactory = ConfigurationFactory
				.forClass(ResourceConfiguration.class, new Validator());

		configurationFactory.build(new File("src/test/resources/fiddle/config/sample-full.config"));
	}
}
