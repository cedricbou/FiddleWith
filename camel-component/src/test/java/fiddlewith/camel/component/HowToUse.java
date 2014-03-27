package fiddlewith.camel.component;

import java.io.File;
import java.net.URL;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import fiddle.repository.manager.FiddlesRepositoryManager;
import fiddle.repository.manager.RepositoryManager;
import fiddle.repository.manager.ResourcesRepositoryManager;
import fiddle.repository.manager.TemplatesRepositoryManager;
import fiddle.ruby.RubyExecutor;

public class HowToUse {

	private static class TestRoute extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			from("direct:start").to("fiddle:demo:add").to("fiddle:demo:pow").process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					System.out.println(exchange.getIn().getBody());
				}
			});
		}
	}

	private final CamelContext camel;

	public HowToUse() throws Exception {
		final URL url = this.getClass().getResource("localised.donoterase");
		final File globalDir = new File(url.getFile()).getParentFile();
		final File repoDir = new File(globalDir, "repository");
		this.camel = new DefaultCamelContext();
		this.camel.addComponent("fiddle",
			new FiddleComponent( 
				new RepositoryManager(
					new FiddlesRepositoryManager(repoDir),
					new TemplatesRepositoryManager(repoDir), 
					new ResourcesRepositoryManager(repoDir, null, null)), 
				new RubyExecutor()));

		this.camel.addRoutes(new TestRoute());
		this.camel.start();
	}

	@Test
	public void tryOut() throws Exception {
		camel.createProducerTemplate().sendBody("direct:start", "{\"a\":5, \"b\":6}");
		camel.createProducerTemplate().sendBody("direct:start", "{\"a\":4, \"b\":3}");
		camel.createProducerTemplate().sendBody("direct:start", "{\"a\":9, \"b\":0}");

	}
}
