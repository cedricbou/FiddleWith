package domain.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;

import com.google.common.base.Optional;

public class FiddleHttpResponse {

	public static interface OnHttpStream {
		public void read(final Optional<String> content);
	}
	
	public static interface OnHttpError {
		public void analyse(final StatusLine statusLine);
	}
	
	public static class Redirect {
		public final int code;
		public final Optional<String> location;
		
		public Redirect(final int code) {
			this.code = code;
			this.location = Optional.absent();
		}

		private Redirect(final int code, Optional<String> location) {
			this.code = code;
			this.location = location;
		}

		public Redirect withLocation(final String location) {
			return new Redirect(code, Optional.fromNullable(location));
		}
		
		@Override
		public String toString() {
			return (code + " - " + location.toString()).trim(); 
		}
	}
	
	public static interface OnHttpRedirect {
		public void toward(final Redirect redirect);
	}

	public final HttpResponse r;

	public FiddleHttpResponse(final HttpResponse r) {
		this.r = r;

	}
	
	public void onError(OnHttpError func) {
		if(isError()) {
			func.analyse(r.getStatusLine());
		}
	}
	
	public void onRedirect(OnHttpRedirect func) {
		if(isRedirect()) {
			final Redirect r = new Redirect(this.r.getStatusLine().getStatusCode());
			final Header[] h = this.r.getHeaders("location");
			if(h != null && h.length > 0) {
				func.toward(r.withLocation(h[0].getValue()));
			}
			else {
				func.toward(r);
			}
		}
	}

	public void onStream(OnHttpStream func) {
		if (isOk()) {
			final HttpEntity entity = r.getEntity();

			if (entity != null) {
				final Optional<Charset> charset = Optional
						.fromNullable(ContentType.getOrDefault(entity)
								.getCharset());

				try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					entity.writeTo(baos);
					func.read(Optional.of(baos.toString((charset
							.or(Charset.defaultCharset())).name())));
				} catch (IOException e) {
					func.read(Optional.<String> absent());
					throw new RuntimeException(e);
				}
			} else {
				func.read(Optional.<String> absent());
			}
		}
	}

	public boolean isOk() {
		return r.getStatusLine().getStatusCode() >= 200
				&& r.getStatusLine().getStatusCode() < 300;
	}
	
	public boolean isError() {
		return r.getStatusLine().getStatusCode() >= 400;
	}
	
	public boolean isRedirect() {
		return r.getStatusLine().getStatusCode() >= 300 && r.getStatusLine().getStatusCode() < 400;
	}
}
