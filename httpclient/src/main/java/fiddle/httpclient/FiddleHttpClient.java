package fiddle.httpclient;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiddleHttpClient {

	private final HttpClient client;

	private final static Logger LOG = LoggerFactory
			.getLogger(FiddleHttpClient.class);

	public FiddleHttpClient(final HttpClient client) {
		this(client, true, true);
	}
	
	public FiddleHttpClient(final HttpClient client,
			final boolean laxRedirectStrategy, final boolean disableSSLCheck) {
		this.client = client;

		if (laxRedirectStrategy) {
			enableLaxRedirectStrategy();
		}

		if (disableSSLCheck) {
			disableSSLCheck();
		}
	}

	private void enableLaxRedirectStrategy() {
		if (client instanceof DefaultHttpClient) {
			((DefaultHttpClient) this.client)
					.setRedirectStrategy(new LaxRedirectStrategy());
		} else {
			LOG.warn("Failed to set lax redirect strategy for httpclient because it is not an instance of HttpDefaultClient");
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
