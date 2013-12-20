package fiddle.httpclient;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiddleHttpClient {

	public enum Redirect {
		LAX(new LaxRedirectStrategy()), DEFAULT(new DefaultRedirectStrategy()), NONE(new RedirectStrategy() {
			@Override
			public boolean isRedirected(HttpRequest request,
					HttpResponse response, HttpContext context)
					throws ProtocolException {
				return false;
			}
			
			@Override
			public HttpUriRequest getRedirect(HttpRequest request,
					HttpResponse response, HttpContext context)
					throws ProtocolException {
				return null;
			}
		});
	
		public final RedirectStrategy s;
		
		private Redirect(final RedirectStrategy s) {
			this.s = s;
		}
	}
	
	public enum SSL { UNCHECKED, DEFAULT }
	
	private final HttpClient client;

	private final static Logger LOG = LoggerFactory
			.getLogger(FiddleHttpClient.class);

	public FiddleHttpClient(final HttpClient client) {
		this(client, Redirect.LAX, SSL.UNCHECKED);
	}
	
	public FiddleHttpClient(final HttpClient client,
			final Redirect redirectStrategy, final SSL sslCheck) {
		this.client = client;

		setRedirectStrategy(redirectStrategy);
	
		if (SSL.UNCHECKED == sslCheck) {
			disableSSLCheck();
		}
	}

	private void setRedirectStrategy(final Redirect redirect) {
		if (client instanceof DefaultHttpClient) {
			((DefaultHttpClient) this.client)
					.setRedirectStrategy(redirect.s);
		} else {
			LOG.warn("Failed to set redirect strategy " + redirect + " for httpclient because it is not an instance of HttpDefaultClient");
		}
	}

	private void disableSSLCheck() {
		if (client instanceof DefaultHttpClient) {
			try {
				SSLSocketFactory socketFactory = new SSLSocketFactory(
						new TrustStrategy() {

							public boolean isTrusted(
									final X509Certificate[] chain,
									String authType)
									throws CertificateException {
								return true;
							}

						},
						org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				((DefaultHttpClient) this.client).getConnectionManager()
						.getSchemeRegistry()
						.register(new Scheme("https", 443, socketFactory));
			} catch (GeneralSecurityException e) {
				LOG.error("failed to disable SSL check for client", e);
			}
		} else {
			LOG.warn("Failed to disable SSL check for httpclient because it is not an instance of HttpDefaultClient");
		}
	}

	public ConfiguredFiddleHttpClient withUrl(final String url) {
		return new ConfiguredFiddleHttpClient(client, url);
	}
}
