package fiddle.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

import wiremock.org.mortbay.log.Log;

import com.google.common.base.Optional;

public class FiddleHttpResponse {

	private final int status;
	private final byte[] body;
	private final Optional<Charset> charset;

	protected FiddleHttpResponse(final HttpResponse response) {
		this.status = response.getStatusLine().getStatusCode();

		final HttpEntity entity = response.getEntity();

		this.charset = Optional.fromNullable(ContentType.getOrDefault(entity)
				.getCharset());

		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			entity.writeTo(baos);
			body = baos.toByteArray();
			// text =
			// Optional.of(baos.toString((charset.or(Charset.defaultCharset())).name()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String body() {
		try {
			return new String(body,
					(charset.or(Charset.defaultCharset())).name());
		} catch (UnsupportedEncodingException ee) {
			Log.warn(
					"failed to create String from found charset ("
							+ charset.toString()
							+ "), will return default encoded string", ee);
			return new String(body);
		}
	}

	public boolean is2XX() {
		return status >= 200 && status < 300;
	}
}
