package domain.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;

import com.google.common.base.Optional;

import fiddle.xml.SimpleXml;

public class FiddleHttpResponse {

	public static interface OnSuccess {
		public void onSuccess(final FiddleHttpResponse response);
	}
	
	public static interface OnError {
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
	
	public static interface OnRedirect {
		public void toward(final Redirect redirect);
	}

	private final HttpResponse r;
	
	private Optional<String> text = Optional.absent();

	public FiddleHttpResponse(final HttpResponse r) {
		this.r = r;
	}
	
	public FiddleHttpResponse onError(OnError func) {
		if(isError()) {
			func.analyse(r.getStatusLine());
		}
		return this;
	}
	
	public FiddleHttpResponse onRedirect(OnRedirect func) {
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
		return this;
	}

	public void onSuccess(OnSuccess func) {
		if (isOk()) {
			func.onSuccess(this);
		}
	}

	public String asText() {
		if(!text.isPresent()) {
			final HttpEntity entity = r.getEntity();
			final Optional<Charset> charset = Optional
					.fromNullable(ContentType.getOrDefault(entity)
							.getCharset());

			try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				entity.writeTo(baos);
				text = Optional.of(baos.toString((charset.or(Charset.defaultCharset())).name()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return text.get();
	}
	
	public SimpleXml asXml() {
		return new SimpleXml(asText());
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
