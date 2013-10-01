package xml.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteStreams;

import fiddle.xml.SimpleXml;

public class HowToUseTest {

	private final String FIXTURE_SIMPLE_XML;

	private final String FIXTURE_DOCUMENT_SOAP_XML;

	public HowToUseTest() throws IOException {
		this.FIXTURE_SIMPLE_XML = new String(ByteStreams.toByteArray(this
				.getClass().getResourceAsStream("FixtureSimpleXml.xml")));
		this.FIXTURE_DOCUMENT_SOAP_XML = new String(
				ByteStreams.toByteArray(this.getClass().getResourceAsStream(
						"FixtureSimpleDocumentSoap.xml")));

	}

	@Test
	public void readSimpleXml() {
		final SimpleXml xml = new SimpleXml(FIXTURE_SIMPLE_XML);

		assertEquals("1.1.1", xml.get("build").get("xml.version").value());

		assertEquals("axiom-impl", xml.get("dependencies").get("dependency")
				.list()[1].get("artifactId").value());

		assertEquals("is present", xml.get("things").get("oneof").get("them")
				.value());
	}

	@Test
	public void readNamespacedXml() {
		final SimpleXml xml = new SimpleXml(FIXTURE_DOCUMENT_SOAP_XML);

		final Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		final int ITER = 1000;
		
		for (int i = 0; i < ITER; ++i) {

			assertEquals("34.5",
					xml.withNS("http://www.w3.org/2001/12/soap-envelope", "ws")
							.withNS("http://www.example.org/stock", "stock")
							.get("ws:Body").get("stock:GetStockPriceResponse")
							.get("stock:Price").value());
		}
		
		System.out.println("Throughput : readnampacesxml : " + ((double)ITER / (double)stopwatch.elapsed(TimeUnit.MICROSECONDS) * 1000000) + " it/sec");
	}

}
