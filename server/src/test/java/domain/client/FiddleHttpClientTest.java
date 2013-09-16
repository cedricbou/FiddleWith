package domain.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.http.StatusLine;
import org.junit.Test;

import com.google.common.base.Optional;
import com.yammer.dropwizard.client.HttpClientBuilder;

import domain.TemplateId;
import domain.WorkspaceId;
import domain.client.FiddleHttpResponse.OnHttpError;
import domain.client.FiddleHttpResponse.OnHttpRedirect;
import domain.client.FiddleHttpResponse.OnHttpStream;
import domain.client.FiddleHttpResponse.Redirect;
import domain.template.FiddleTemplateRenderer;

public class FiddleHttpClientTest {
	
	@Test
	public void testGooglePost() throws URISyntaxException {
		final FiddleTemplateRenderer renderer = new FiddleTemplateRenderer() {
			@Override
			public void render(Object entity, WorkspaceId workspaceId,
					TemplateId templateId, Locale locale, OutputStream output)
					throws IOException {
				output.write("{\"f\":\"sqsds\", \"g\":\"bx\"}".getBytes());
				output.close();
			}
		};
		
		final FiddleHttpClient client = new FiddleHttpClient(renderer, new HttpClientBuilder().build());
		
		final FiddleHttpResponse r = client.post(new URI("http://www.securitoo.com"), com.google.common.net.MediaType.JSON_UTF_8, new TemplateId(""), null);
		
		r.onStream(new OnHttpStream() {
			@Override
			public void read(Optional<String> content) {
				System.out.println(">>>> " + content.orNull() + " <<<<<");
			}
		});
		
		r.onError(new OnHttpError() {
			
			@Override
			public void analyse(StatusLine statusLine) {
				System.out.println("!!!! " + statusLine.toString());
			}
		});
		
		r.onRedirect(new OnHttpRedirect() {
			
			@Override
			public void toward(Redirect redirect) {
				System.out.println("---> " + redirect.toString());
			}
		});


	}

}
